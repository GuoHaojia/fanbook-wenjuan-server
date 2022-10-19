package com.tduck.cloud.api.web.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.tduck.cloud.account.entity.UserEntity;
import com.tduck.cloud.account.service.UserService;
import com.tduck.cloud.account.vo.UserRoleVo;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.api.annotation.NoRepeatSubmit;
import com.tduck.cloud.api.util.HttpUtils;
import com.tduck.cloud.common.constant.CommonConstants;
import com.tduck.cloud.common.email.MailService;
import com.tduck.cloud.common.util.RedisUtils;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.common.validator.ValidatorUtils;
import com.tduck.cloud.project.entity.*;
import com.tduck.cloud.project.entity.enums.ProjectLogicExpressionEnum;
import com.tduck.cloud.project.request.QueryProjectResultRequest;
import com.tduck.cloud.project.service.*;
import com.tduck.cloud.project.vo.ExportProjectResultVO;
import com.tduck.cloud.wx.mp.service.WxMpUserMsgService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import static com.tduck.cloud.project.constant.ProjectRedisKeyConstants.PROJECT_VIEW_IP_LIST;

/**
 * @author : smalljop
 * @description : 项目
 * @create : 2020-11-18 18:17
 **/

@Slf4j
@Data
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/project/result")
public class UserProjectResultController {

    private final UserProjectService projectService;
    private final UserProjectResultService projectResultService;
    private final UserProjectSettingService userProjectSettingService;
    private final MailService mailService;
    private final WxMpUserMsgService userMsgService;
    private final RedisUtils redisUtils;


    private final UserProjectLogicService projectLogicService;
    private final UserService userService;

    private final ProjectPrizeSettingService projectPrizeSettingService;
    private final ProjectPrizeItemService projectPrizeItemService;

    @Value("${devdebug}")
    private boolean debug;

    /***
     * 查看项目
     *  记录查看的IP 统计查看用户数
     * @return
     */
    @PostMapping("view/{projectKey}")
    public Result viewProject(HttpServletRequest request, @PathVariable("projectKey") String projectKey) {
        String ip = HttpUtils.getIpAddr(request);
        Integer count = Convert.toInt(redisUtils.hmGet(StrUtil.format(PROJECT_VIEW_IP_LIST, projectKey), ip), CommonConstants.ConstantNumber.ZERO);
        redisUtils.hmSet(StrUtil.format(PROJECT_VIEW_IP_LIST, projectKey), ip, count + CommonConstants.ConstantNumber.ONE);
        return Result.success();
    }


    /**
     * 填写
     *
     * @param entity
     * @param request
     * @return
     */
    @NoRepeatSubmit
    @PostMapping("/create")
    public Result createProjectResult(@RequestBody UserProjectResultEntity entity, HttpServletRequest request) {

        //本地测试
        if(debug){
            entity.setFbUserid("416120040304148480");
            entity.setFbUsername("3904464");
            entity.setGuildId("420861300550139904");
            entity.setGuildName("测试服务");
        }

        ValidatorUtils.validateEntity(entity);
        entity.setSubmitRequestIp(HttpUtils.getIpAddr(request));

        //校验时间 填写次数等
        Result<UserProjectSettingEntity> userProjectSettingStatus = userProjectSettingService.getUserProjectSettingStatus(entity.getProjectKey(), entity.getSubmitRequestIp(), entity.getFbUserid());
        if (StrUtil.isNotBlank(userProjectSettingStatus.getMsg())) {
            return Result.failed(userProjectSettingStatus.getMsg());
        }

        projectResultService.saveProjectResult(entity);


        ///fbuserid 转uid
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fb_user", entity.getFbUserid());
        UserEntity userEntity = userService.getOne(queryWrapper);

        //分配角色内容
        List<UserProjectLogicEntity> entityList = projectLogicService.list(Wrappers.<UserProjectLogicEntity>lambdaQuery().eq(UserProjectLogicEntity::getProjectKey, entity.getProjectKey()).eq(UserProjectLogicEntity::getType,3));

        for(UserProjectLogicEntity logic : entityList){
            //每个角色逻辑

            Set<UserProjectLogicEntity.Condition> set = logic.getConditionList();

            Integer flag = 0;
            //逻辑集合遍历
            for (UserProjectLogicEntity.Condition item : JSON.parseArray(JSON.toJSONString(set), UserProjectLogicEntity.Condition.class)){

                //任意逻辑 满足  就分配角色
                if(logic.getExpression().equals(ProjectLogicExpressionEnum.ANY) && logicItem(item,entity.getOriginalData())){
                    flag = 2;
                    break;
                }

                //全部逻辑
                if(logic.getExpression().equals(ProjectLogicExpressionEnum.ALL)){
                    //逻辑犯错 就终止
                    if(logicItem(item,entity.getOriginalData())){
                        flag = 1;
                        continue;
                    }else{
                        flag = 0;
                        break;
                    }
                }
            }

            if(logic.getExpression().equals(ProjectLogicExpressionEnum.ALL) && flag == 1) {
                flag = 2;
            }

            //构建角色 赋予用户  或者不走逻辑判断
            if(flag == 2 || logic.getRoleType() == false){

                UserRoleVo userRoleVo = new UserRoleVo();
                userRoleVo.setUserid(userEntity.getId());
                userRoleVo.setRoleid(logic.getFormItemId());
                userRoleVo.setRolestatus(1);

                userService.updateUserBelong(userRoleVo);
            }
        }
        //分配角色内容 end


        ThreadUtil.execAsync(() -> {
            //异步邮件通知
            UserProjectSettingEntity settingEntity = userProjectSettingStatus.isDataNull() ? null : userProjectSettingStatus.getData();
            this.sendWriteResultNotify(settingEntity, entity);
        });

        //结算奖励 奖励同步返回
        List<ProjectPrizeSettingEntity> settingList = projectPrizeSettingService.lambdaQuery().eq(ProjectPrizeSettingEntity::getProjectKey,entity.getProjectKey()).list();
        if(settingList.size() > 0){
            ProjectPrizeSettingEntity setting = settingList.get(0);

            if(setting.getType() == 1 )
            {
                ///立即发放
                if(setting.getProbability() == 1 || (int)Math.random()*(setting.getProbability()+1) == 1)
                {
                    //逻辑中奖
                    List<ProjectPrizeItemEntity> prizeList = projectPrizeItemService.lambdaQuery()
                            .eq(ProjectPrizeItemEntity::getProjectKey,entity.getProjectKey())
                            .eq(ProjectPrizeItemEntity::getFanbookid,"")
                            .eq(ProjectPrizeItemEntity::getStatus,true).list();

                    if(prizeList.size() > 0)
                    {
                        ProjectPrizeItemEntity prizeItem = prizeList.get(0);
                        ProjectPrizeItemEntity newItem = ProjectPrizeItemEntity.builder()
                                .phoneNumber(userEntity.getPhoneNumber())
                                .fanbookid(entity.getFbUserid())
                                .nickname(userEntity.getName())
                                .getTime(LocalDateTime.now())
                                .build();
                        Boolean result = projectPrizeItemService.lambdaUpdate().eq(ProjectPrizeItemEntity::getId,prizeItem.getId())
                                .update(newItem);

                        if(result){
                            if(prizeItem.getType() == 1){
                                //添加积分  等待甲方api

                            }

                            //中奖了 如果是积分
                            return Result.success(newItem);
                        }
                    }
                }
            }
        }
        //结算奖励 奖励同步返回

        return Result.success();
    }

    //逻辑判断子方法 判断每个判断条件是否生效
    private Boolean logicItem(UserProjectLogicEntity.Condition item, Map<String, Object> originalData){
        ///相等情况  满足一个就返回
        if(item.getExpression().equals("eq")){
            if(originalData.get("field"+item.getFormItemId()) instanceof List<?>){

                for( Object formid : (List<?>)originalData.get("field"+item.getFormItemId())){
                    if(item.getOptionValue() == formid){
                        return true;
                    }
                }

                return false;
            }else{
                if(item.getOptionValue() == originalData.get("field"+item.getFormItemId())){
                    return true;
                }else{
                    return false;
                }
            }
        }

        //不等情况下 取反
        if(item.getExpression().equals("ne")){
            if(originalData.get("field"+item.getFormItemId()) instanceof List<?>){

                for( Object formid : (List<?>)originalData.get("field"+item.getFormItemId())){
                    if(item.getOptionValue() == formid){
                        return false;
                    }
                }
                return true;
            }else{
                if(item.getOptionValue() == originalData.get("field"+item.getFormItemId())){
                    return false;
                }else{
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 填写结果excel导出
     *
     * @param request
     * @return
     */
    @GetMapping("/export")
    public void exportProjectResult(QueryProjectResultRequest request, HttpServletResponse response) throws IOException {
        ExportProjectResultVO exportProjectResultVO = projectResultService.exportProjectResult(request);
        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter();
        //自定义标题别名
        exportProjectResultVO.getTitleList().forEach(item -> {
            writer.addHeaderAlias(item.getFieldKey(), item.getTitle());
        });
        //设置每列默认宽度
        writer.setColumnWidth(-1, 20);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(exportProjectResultVO.getResultList(), true);
        //out为OutputStream，需要写出到的目标流
        //response为HttpServletResponse对象
        response.setContentType("application/octet-stream;charset=utf-8");
        //设置下载的文件名
//        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    /**
     * 填写附件导出
     *
     * @param request
     * @return
     */
    @Login
    @GetMapping("/download/file")
    public Result downloadProjectResultFile(QueryProjectResultRequest request) {
        return projectResultService.downloadProjectResultFile(request);
    }


    /**
     * 结果分页
     *
     * @param request
     * @return
     */
    @Login
    @GetMapping("/page")
    public Result queryProjectResults(QueryProjectResultRequest request) {
        return Result.success(projectResultService.listByQueryConditions(request));
    }


    /**
     * 查询公开结果
     *
     * @param request
     * @return
     */
    @GetMapping("/public/page")
    public Result queryProjectPublicResults(QueryProjectResultRequest request) {
        UserProjectSettingEntity settingEntity = userProjectSettingService.getByProjectKey(request.getProjectKey());
        if (!settingEntity.getPublicResult()) {
            return Result.success();
        }
        return Result.success(projectResultService.listByQueryConditions(request));
    }

    private void sendWriteResultNotify(UserProjectSettingEntity settingEntity, UserProjectResultEntity entity) {
        if (ObjectUtil.isNull(settingEntity)) {
            return;
        }
        String projectKey = entity.getProjectKey();
        UserProjectEntity project = projectService.getByKey(projectKey);
        if (StrUtil.isNotBlank(settingEntity.getNewWriteNotifyEmail())) {
            mailService.sendTemplateHtmlMail(settingEntity.getNewWriteNotifyEmail(), "新回复通知", "mail/project-write-notify", MapUtil.of("projectName", project.getName()));
        }

        if (StrUtil.isNotBlank(settingEntity.getNewWriteNotifyWx())) {
            List<String> openIdList = StrUtil.splitTrim(settingEntity.getNewWriteNotifyWx(), ";");
            openIdList.stream().forEach(openId -> {
                userMsgService.sendKfTextMsg("", openId, "收到新的反馈，请去Pc端查看");
            });
        }
    }


}