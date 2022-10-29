package com.tduck.cloud.api.web.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.tduck.cloud.account.entity.UserEntity;
import com.tduck.cloud.account.service.UserService;
import com.tduck.cloud.account.vo.UserRoleVo;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.api.annotation.NoRepeatSubmit;
import com.tduck.cloud.api.util.HttpUtils;
import com.tduck.cloud.api.web.fb.service.OauthService;
import com.tduck.cloud.common.constant.CommonConstants;
import com.tduck.cloud.common.email.MailService;
import com.tduck.cloud.common.exception.BaseException;
import com.tduck.cloud.common.util.RedisUtils;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.common.validator.ValidatorUtils;
import com.tduck.cloud.project.constant.ProjectRedisKeyConstants;
import com.tduck.cloud.project.entity.*;
import com.tduck.cloud.project.entity.enums.ProjectLogicExpressionEnum;
import com.tduck.cloud.project.entity.struct.RadioExpandStruct;
import com.tduck.cloud.project.request.QueryProjectResultRequest;
import com.tduck.cloud.project.service.*;
import com.tduck.cloud.project.vo.DxCountVo;
import com.tduck.cloud.project.vo.ExportProjectResultVO;
import com.tduck.cloud.wx.mp.service.WxMpUserMsgService;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import static com.tduck.cloud.project.constant.ProjectRedisKeyConstants.*;

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
    private final UserProjectItemService projectItemService;
    private final UserPublishService userPublishService;
    private final UserProjectLogicService projectLogicService;
    private final UserService userService;
    private final ProjectPrizeSettingService projectPrizeSettingService;
    private final ProjectPrizeService projectPrizeService;
    private final ProjectPrizeItemService projectPrizeItemService;
    private final OauthService oauthService;
    private final LinkedBlockingQueue<UserProjectResultEntity> queue = new LinkedBlockingQueue<>();

    @Value("${devdebug}")
    private boolean debug;

    @Value("${fb.open.api.access_token}")
    private String access_token;

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
//        AtomicInteger count = new AtomicInteger();

        //本地测试
        /*if(debug){
            entity.setFbUserid("416120040304148480");
            entity.setFbUsername("3904464");
            entity.setGuildId("420861300550139904");
            entity.setGuildName("测试服务");
        }*/

        ValidatorUtils.validateEntity(entity);
        entity.setSubmitRequestIp(HttpUtils.getIpAddr(request));

        //校验时间 填写次数等
        Result<UserProjectSettingEntity> userProjectSettingStatus = userProjectSettingService.getUserProjectSettingStatus(entity.getProjectKey(), entity.getSubmitRequestIp(), entity.getFbUserid());
        if (StrUtil.isNotBlank(userProjectSettingStatus.getMsg())) {
            return Result.failed(userProjectSettingStatus.getMsg());
        }

        Boolean save = projectResultService.saveProjectResult(entity);

        if (BooleanUtil.isTrue(save)) {
            queue.offer(entity);
            UserProjectResultEntity poll = queue.poll();

            //答卷数量
            this.setResultNum(poll);
            //答卷统计
            this.calculateProjectResult(poll);
        }

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

                //分配角色
                oauthService.setMemberRoles(access_token,Long.valueOf(entity.getGuildId()),Long.valueOf(entity.getFbUserid()),logic.getFormItemId());
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
                        //有限发奖
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
                                //添加积分
                                oauthService.modifyUserPoint(prizeItem.getId()+"",Long.valueOf(entity.getGuildId()),Long.valueOf(entity.getFbUserid()),Integer.valueOf(prizeItem.getPrize()),"奖励积分");
                            }

                            //中奖了 如果是积分
                            return Result.success(newItem);
                        }
                    }else{
                        //加载无限积分发奖
                        List<ProjectPrizeEntity> unlimitList = projectPrizeService.lambdaQuery()
                                .eq(ProjectPrizeEntity::getProjectKey,entity.getProjectKey())
                                .eq(ProjectPrizeEntity::getCount,0)
                                .eq(ProjectPrizeEntity::getStatus,true).list();

                        if(unlimitList.size() > 0){
                            ProjectPrizeEntity unlimit = unlimitList.get(0);

                            ProjectPrizeItemEntity prizeItem = ProjectPrizeItemEntity.builder()
                                    .id(null)
                                    .prizeid(unlimit.getId())
                                    .prize(unlimit.getDesc())
                                    .projectKey(entity.getProjectKey())
                                    .phoneNumber(userEntity.getPhoneNumber())
                                    .fanbookid(entity.getFbUserid())
                                    .nickname(userEntity.getName())
                                    .getTime(LocalDateTime.now())
                                    .status(true)
                                    .type(1)
                                    .build();

                            Boolean result = projectPrizeItemService.save(prizeItem);
                            if(result){
                                if(prizeItem.getType() == 1){
                                    //添加积分
                                    oauthService.modifyUserPoint(prizeItem.getId()+"",Long.valueOf(entity.getGuildId()),Long.valueOf(entity.getFbUserid()),Integer.valueOf(prizeItem.getPrize()),"奖励积分");
                                }
                            }
                            return Result.success(prizeItem);
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
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("答卷数据.xls", "utf-8"));
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

    /**
     * 填写结果数据
     *
     * @param key
     * @return
     */
    @Login
    @GetMapping("/data")
    public Result queryProjectResultDate(@RequestParam("key") String key) {

        return Result.success(projectResultService.queryProjectResultDate(key));
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

    //答题数
    public void setResultNum(UserProjectResultEntity entity) {
        //总答题数
        UserProjectEntity one = projectService.getOne(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, entity.getProjectKey()));
//        one.setAnswerNum(count.incrementAndGet());
        one.setAnswerNum((redisUtils.get(StrUtil.format(ProjectRedisKeyConstants.PROJECT_RESULT_NUMBER, entity.getProjectKey()), Integer.class)));
        projectService.updateById(one);
        //各推送答卷答题数
        PublishEntity ps= userPublishService.getOne(Wrappers.<PublishEntity>lambdaQuery().eq(PublishEntity::getKey, entity.getProjectKey()).eq(PublishEntity::getGuildId, entity.getGuildId()).eq(PublishEntity::getFbChannel, entity.getChatId()).eq(PublishEntity::getPublishTime, entity.getPublishTime()));
        if (ObjectUtil.isNotNull(ps)) {
            ps.setAnswerNum((int) redisUtils.incr(StrUtil.format(ProjectRedisKeyConstants.PROJECT_RESULT_NUMBER, ps.getId()), CommonConstants.ConstantNumber.ONE));
            log.error("无此统计数据");
        }
    }

    //答卷数据统计
    public void calculateProjectResult(UserProjectResultEntity entity) {
//        UserProjectResultEntity entity = queue.poll();
        if (ObjectUtil.isNotNull(entity)) {
            //数据
            List<UserProjectItemEntity> itemEntityList = projectItemService.list(Wrappers.<UserProjectItemEntity>lambdaQuery()
                    .eq(UserProjectItemEntity::getProjectKey, entity.getProjectKey())
                    .orderByAsc(UserProjectItemEntity::getSort)
            );

            Map<String, Object> originalData = entity.getOriginalData();
            System.out.println(originalData);
            if (ObjectUtil.isNull(originalData)) {
                log.error("数据为空，请填写问卷");
            }
            //防止答题后表单变化，对比答题数据和表单项（bug:答卷推送后，表单还能编辑，formid不唯一）
            for (Map.Entry<String, Object> entry : originalData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                itemEntityList.forEach(item -> {
                    if (key.substring(5,8).equals(String.valueOf(item.getFormItemId())) && ObjectUtil.isNotNull(value)) {
                        String type = item.getType().getValue();
                        //题目计数
                        redisUtils.incr(StrUtil.format("PROJECT_RESULT_"+type+":{}", item.getProjectKey()+"/"+item.getId()), CommonConstants.ConstantNumber.ONE);
                        //多选 单选 下拉 图片选择 评分
                        if (type == "CHECKBOX" || type == "RADIO" || type == "SELECT" || type == "IMAGE_SELECT" ||type == "RATE") {
                            //选项计数
                            this.redisIncr(value, item, type);
                        }
                        //矩阵量表
                        if (item.getType().equals("MATRIX_SCALE")) {
                            this.redisIncr(value, item, "TEXTAREA");
                        }
                        //矩阵选择
                        if (item.getType().equals("MATRIX_SELECT")) {
                            this.redisIncr(value, item, "TEXTAREA");
                        }
                    }
                });
            }
        }
    }

    /**
     * 选项计数
     */
    public void redisIncr(Object value, UserProjectItemEntity item, String type) {
        //排除答题单、双选设置
        if (value instanceof List) {
            List jsonObject = JSONArray.parseObject(JSON.toJSONString(value), List.class);
            System.out.println(jsonObject);
            jsonObject.forEach(index -> {
                System.out.println(index);
                //(index)排除选项同名bug
                redisUtils.incr(StrUtil.format("PROJECT_RESULT_"+type+":{}", item.getProjectKey()+"/"+item.getId()+"/"+index), CommonConstants.ConstantNumber.ONE);
            });
        } else {
            redisUtils.incr(StrUtil.format("PROJECT_RESULT_"+type+":{}", item.getProjectKey()+"/"+item.getId()+"/"+value), CommonConstants.ConstantNumber.ONE);
        }
    }


}