package com.tduck.cloud.api.web.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiniu.util.Json;
import com.tduck.cloud.account.entity.UserEntity;
import com.tduck.cloud.account.service.UserService;
import com.tduck.cloud.account.vo.Chat;
import com.tduck.cloud.account.vo.UserRoleVo;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.api.annotation.NoRepeatSubmit;
import com.tduck.cloud.api.util.HttpUtils;
import com.tduck.cloud.api.web.fb.service.OauthService;
import com.tduck.cloud.common.constant.CommonConstants;
import com.tduck.cloud.common.constant.FanbookCard;
import com.tduck.cloud.common.email.MailService;
import com.tduck.cloud.common.exception.BaseException;
import com.tduck.cloud.common.util.JsonUtils;
import com.tduck.cloud.common.util.RedisUtils;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.common.validator.ValidatorUtils;
import com.tduck.cloud.project.constant.ProjectRedisKeyConstants;
import com.tduck.cloud.project.entity.*;
import com.tduck.cloud.project.entity.enums.ProjectLogicExpressionEnum;
import com.tduck.cloud.project.entity.enums.ProjectStatusEnum;
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
 * @description : ??????
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


    private final FanbookService fanbookService;
    @Value("${devdebug}")
    private boolean debug;

    @Value("${fb.bot.token}")
    private String access_token;

    @Value("${fb.open.api.scoreredirecthost}")
    private String scoreHost;

    /***
     * ????????????
     *  ???????????????IP ?????????????????????
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
     * ??????
     *
     * @param entity
     * @param request
     * @return
     */
    @NoRepeatSubmit
    @PostMapping("/create")
    public Result createProjectResult(@RequestBody UserProjectResultEntity entity, HttpServletRequest request) {
//        AtomicInteger count = new AtomicInteger();

        //????????????
        /*if(debug){
            entity.setFbUserid(416120040304148480L);
            entity.setFbUsername("???????????????");
            entity.setGuildId(420861300550139904L);
            entity.setGuildName("????????????");
        }*/

        ValidatorUtils.validateEntity(entity);
        UserProjectEntity one = projectService.getOne(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, entity.getProjectKey()));
        if (one.getStatus() == ProjectStatusEnum.STOP) {
            return Result.failed("?????????????????????????????????");
        }
        entity.setSubmitRequestIp(HttpUtils.getIpAddr(request));

        //???????????? ???????????????
        Result<UserProjectSettingEntity> userProjectSettingStatus = userProjectSettingService.getUserProjectSettingStatus(entity.getProjectKey(), entity.getSubmitRequestIp(), entity.getFbUserid());
        if (StrUtil.isNotBlank(userProjectSettingStatus.getMsg())) {
            return Result.failed(userProjectSettingStatus.getMsg());
        }

        Boolean save = projectResultService.saveProjectResult(entity);

        if (BooleanUtil.isTrue(save)) {
            log.info("?????????????????????????????????");
            queue.offer(entity);
            UserProjectResultEntity poll = queue.poll();

            //????????????
            this.setResultNum(poll);
            //????????????
            this.calculateProjectResult(poll);
        } else {
            return Result.failed("????????????????????????");
        }

        ///fbuserid ???uid
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fb_user", entity.getFbUserid());
        UserEntity userEntity = userService.getOne(queryWrapper);


        if(entity.getFbUserid() != null && entity.getGuildId() != null && entity.getFbUserid() > 0 && entity.getGuildId()>0){
            //??????????????????
            List<UserProjectLogicEntity> entityList = projectLogicService.list(Wrappers.<UserProjectLogicEntity>lambdaQuery().eq(UserProjectLogicEntity::getProjectKey, entity.getProjectKey()).eq(UserProjectLogicEntity::getType,3));

            for(UserProjectLogicEntity logic : entityList){
                //??????????????????

                Set<UserProjectLogicEntity.Condition> set = logic.getConditionList();

                Integer flag = 0;
                //??????????????????
                for (UserProjectLogicEntity.Condition item : JSON.parseArray(JSON.toJSONString(set), UserProjectLogicEntity.Condition.class)){

                    //???????????? ??????  ???????????????
                    if(logic.getExpression().equals(ProjectLogicExpressionEnum.ANY) && logicItem(item,entity.getOriginalData())){
                        flag = 2;
                        break;
                    }

                    //????????????
                    if(logic.getExpression().equals(ProjectLogicExpressionEnum.ALL)){
                        //???????????? ?????????
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

                //???????????? ????????????  ????????????????????????
                if(flag == 2 || logic.getRoleType() == false){

                    //????????????
                    Boolean result = oauthService.setMemberRoles(access_token,Long.valueOf(entity.getGuildId()),Long.valueOf(entity.getFbUserid()),logic.getFormItemId());

                    if (result == null || result == false){
                        Logger.getLogger("????????????").info("??????????????????");
                    }else{
                        Logger.getLogger("????????????").info("??????????????????");
                    }
                }

            }
            //?????????????????? end
        }



        ThreadUtil.execAsync(() -> {
            //??????????????????
            UserProjectSettingEntity settingEntity = userProjectSettingStatus.isDataNull() ? null : userProjectSettingStatus.getData();
            this.sendWriteResultNotify(settingEntity, entity);
        });

        //???????????? ??????????????????
        List<ProjectPrizeSettingEntity> settingList = projectPrizeSettingService.lambdaQuery().eq(ProjectPrizeSettingEntity::getProjectKey,entity.getProjectKey()).list();
        if(entity.getFbUserid() != null && settingList.size() > 0 && entity.getFbUserid()>0){
            ProjectPrizeSettingEntity setting = settingList.get(0);

            if(setting.getType() == 1 )
            {
                ///????????????
                if(setting.getProbability() == 1 || (int)Math.random()*(setting.getProbability()+1) == 1)
                {
                    //????????????
                    List<ProjectPrizeItemEntity> prizeList = projectPrizeItemService.lambdaQuery()
                            .eq(ProjectPrizeItemEntity::getProjectKey,entity.getProjectKey())
                            .eq(ProjectPrizeItemEntity::getFanbookid,"")
                            .eq(ProjectPrizeItemEntity::getStatus,true).list();

                    if(prizeList.size() > 0)
                    {
                        //????????????
                        ProjectPrizeItemEntity prizeItem = prizeList.get(0);
                        ProjectPrizeItemEntity newItem = ProjectPrizeItemEntity.builder()
                                .phoneNumber(userEntity.getPhoneNumber())
                                .fanbookid(entity.getFbUserid()+"")
                                .nickname(userEntity.getName())
                                .getTime(LocalDateTime.now())
                                .build();
                        Boolean result = projectPrizeItemService.lambdaUpdate().eq(ProjectPrizeItemEntity::getId,prizeItem.getId())
                                .update(newItem);

                        if(result){
                            if(prizeItem.getType() == 1){
                                //????????????
                                oauthService.modifyUserPoint(prizeItem.getId()+"",Long.valueOf(entity.getGuildId()),Long.valueOf(entity.getFbUserid()),Integer.valueOf(prizeItem.getPrize()),"????????????");
                                notifyUser(entity.getFbUserid(),one.getName(),prizeItem.getPrize()+"??????",true);
                            }else{
                                notifyUser(entity.getFbUserid(),one.getName(),prizeItem.getPrize(),false);
                            }

                            //????????? ???????????????
                            return Result.success(newItem);
                        }
                    }else{
                        //????????????????????????
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
                                    .fanbookid(entity.getFbUserid()+"")
                                    .nickname(userEntity.getName())
                                    .getTime(LocalDateTime.now())
                                    .status(true)
                                    .type(1)
                                    .build();

                            Boolean result = projectPrizeItemService.save(prizeItem);
                            if(result){
                                if(prizeItem.getType() == 1){
                                    //????????????
                                    oauthService.modifyUserPoint(prizeItem.getId()+"",Long.valueOf(entity.getGuildId()),Long.valueOf(entity.getFbUserid()),Integer.valueOf(prizeItem.getPrize()),"????????????");
                                    notifyUser(entity.getFbUserid(),one.getName(),prizeItem.getPrize()+"??????",true);
                                }else{
                                    notifyUser(entity.getFbUserid(),one.getName(),prizeItem.getPrize(),false);
                                }
                            }
                            return Result.success(prizeItem);
                        }
                    }
                }
            }
        }
        //???????????? ??????????????????

        return Result.success();
    }

    public void notifyUser(Long user_id ,String projectName , String text , Boolean isScore){
        //????????????
        Chat chat = oauthService.getPrivateChat(access_token,user_id);
        JSONObject jsonObject = new JSONObject();
        JSONObject cardJson;
        JSONObject taskJson = new JSONObject();
        if(isScore){
            cardJson = FanbookCard.getPrizeString("?????????????????????"+projectName+"???????????????"+text+"?????????????????????????????????????????????????????????", chat.getId()+"");

            jsonObject = cardJson;
        }else{
            cardJson = FanbookCard.getCdkString("??????????????????"+projectName+"????????????CDK???????????????????????????????????????????????????????????????",text, chat.getId()+"");

            jsonObject = cardJson;
        }


        String rstr = fanbookService.sendMessage(jsonObject);
        Logger.getLogger("??????????????????").info(rstr);
    }

    //????????????????????? ????????????????????????????????????
    private Boolean logicItem(UserProjectLogicEntity.Condition item, Map<String, Object> originalData){
        ///????????????  ?????????????????????
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

        //??????????????? ??????
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
     * ????????????excel??????
     *
     * @param request
     * @return
     */
    @GetMapping("/export")
    public void exportProjectResult(QueryProjectResultRequest request, HttpServletResponse response) throws IOException {
        ExportProjectResultVO exportProjectResultVO = projectResultService.exportProjectResult(request);
        // ?????????????????????writer???????????????xls??????
        ExcelWriter writer = ExcelUtil.getWriter();
        //?????????????????????
        exportProjectResultVO.getTitleList().forEach(item -> {
            writer.addHeaderAlias(item.getFieldKey(), item.getTitle());
        });
        //????????????????????????
        writer.setColumnWidth(-1, 20);
        // ???????????????????????????????????????????????????????????????
        writer.write(exportProjectResultVO.getResultList(), true);
        //out???OutputStream??????????????????????????????
        //response???HttpServletResponse??????
        response.setContentType("application/octet-stream;charset=utf-8");
        //????????????????????????
//        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls??????????????????????????????????????????????????????????????????????????????
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode("????????????.xls", "utf-8"));
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // ??????writer???????????????
        writer.close();
        //????????????????????????Servlet???
        IoUtil.close(out);
    }

    /**
     * ??????????????????
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
     * ????????????
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
     * ??????????????????
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
     * ??????????????????
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
            mailService.sendTemplateHtmlMail(settingEntity.getNewWriteNotifyEmail(), "???????????????", "mail/project-write-notify", MapUtil.of("projectName", project.getName()));
        }

        if (StrUtil.isNotBlank(settingEntity.getNewWriteNotifyWx())) {
            List<String> openIdList = StrUtil.splitTrim(settingEntity.getNewWriteNotifyWx(), ";");
            openIdList.stream().forEach(openId -> {
                userMsgService.sendKfTextMsg("", openId, "???????????????????????????Pc?????????");
            });
        }
    }

    //?????????
    public void setResultNum(UserProjectResultEntity entity) {
        //????????????
        UserProjectEntity one = projectService.getOne(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, entity.getProjectKey()));
//        one.setAnswerNum(count.incrementAndGet());
        one.setAnswerNum((redisUtils.get(StrUtil.format(ProjectRedisKeyConstants.PROJECT_RESULT_NUMBER, entity.getProjectKey()), Integer.class)));
        projectService.updateById(one);
        //????????????????????????
        PublishEntity ps= userPublishService.getOne(Wrappers.<PublishEntity>lambdaQuery().eq(PublishEntity::getKey, entity.getProjectKey()).eq(PublishEntity::getGuildId, entity.getGuildId()).eq(PublishEntity::getFbChannel, entity.getChatId()).eq(PublishEntity::getPublishTime, entity.getPublishTime()));
        if (ObjectUtil.isNull(ps)) {
            log.error("?????????????????????");
        } else {
            ps.setAnswerNum((int) redisUtils.incr(StrUtil.format(ProjectRedisKeyConstants.PROJECT_RESULT_NUMBER, ps.getId()), CommonConstants.ConstantNumber.ONE));
            userPublishService.updateById(ps);
        }
    }

    //??????????????????
    public void calculateProjectResult(UserProjectResultEntity entity) {
        if (ObjectUtil.isNotNull(entity)) {
            //??????
            List<UserProjectItemEntity> itemEntityList = projectItemService.list(Wrappers.<UserProjectItemEntity>lambdaQuery()
                    .eq(UserProjectItemEntity::getProjectKey, entity.getProjectKey())
                    .orderByAsc(UserProjectItemEntity::getSort)
            );

            Map<String, Object> originalData = entity.getOriginalData();
            System.out.println(originalData);
            if (ObjectUtil.isNull(originalData)) {
                log.error("??????????????????????????????");
            }
            //???????????????????????????????????????????????????????????????bug:???????????????????????????????????????formid????????????
            for (Map.Entry<String, Object> entry : originalData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                itemEntityList.forEach(item -> {
                    if (key.substring(5,8).equals(String.valueOf(item.getFormItemId())) && ObjectUtil.isNotNull(value)) {
                        String type = item.getType().getValue();
                        String pkey = item.getProjectKey();
                        Long id = item.getId();

                        if (type == "RATE" && value.equals(0)) {
                        } else {
                            //????????????
                            redisUtils.incr(StrUtil.format("PROJECT_RESULT_"+type+":{}", pkey+"/"+id), CommonConstants.ConstantNumber.ONE);
                            //?????? ?????? ?????? ???????????? ??????
                            if (type == "CHECKBOX" || type == "RADIO" || type == "SELECT" || type == "IMAGE_SELECT" ||type == "RATE") {
                                //????????????
                                this.redisIncr(value, pkey, id, type);
                            }
                            //????????????
                            if (type == "MATRIX_SCALE") {
                                Map<String, Object> parse = JsonUtils.jsonToMap(JSON.toJSONString(value));
                                System.out.println(parse);
                                parse.forEach((k, v) -> {
                                    System.out.println(k);
                                    System.out.println(v);
                                    if (Integer.parseInt(v.toString())!=0) {
                                        //???????????????
                                        this.redisIn(type, pkey, id, k, null);
                                        //???????????????
                                        this.redisIn(type, pkey, id, k, v);
                                    }
                                });
                            }
                            //????????????
                            if (type =="MATRIX_SELECT") {
                                Map<String, Object> parse = JsonUtils.jsonToMap(JSON.toJSONString(value));
                                System.out.println(parse);
                                parse.forEach((k, v) -> {
                                    System.out.println(k);
                                    System.out.println(v);
                                    List v1 = (List) v;
                                    if (CollectionUtil.isNotEmpty(v1)) {
                                        //???????????????
                                        this.redisIn(type, pkey, id, k, null);
                                        //???????????????
                                        v1.forEach(l -> {
                                            this.redisIn(type, pkey, id, k, l);
                                        });
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * ????????????
     */
    public void redisIncr(Object value, String pkey, Long id, String type) {
        //??????????????????????????????
        if (value instanceof List) {
            List jsonObject = JSONArray.parseObject(JSON.toJSONString(value), List.class);
            jsonObject.forEach(index -> {
                //(index)??????????????????bug
                this.redisIn(type, pkey, id, index, null);
            });
        } else {
            this.redisIn(type, pkey, id, value, null);
        }
    }

    /**
     * ????????????
     */
    public void redisIn(String type, String pkey, Long id, Object k, Object v){
        if (ObjectUtil.isNull(v)) {
            redisUtils.incr(StrUtil.format("PROJECT_RESULT_"+type+":{}", pkey+"/"+id+"/"+k), CommonConstants.ConstantNumber.ONE);
        } else {
            redisUtils.incr(StrUtil.format("PROJECT_RESULT_"+type+":{}", pkey+"/"+id+"/"+k+"/"+v), CommonConstants.ConstantNumber.ONE);
        }
    }


}