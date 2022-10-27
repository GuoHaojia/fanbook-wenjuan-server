package com.tduck.cloud.api.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.Sets;
import com.tduck.cloud.account.entity.UserEntity;
import com.tduck.cloud.account.service.UserService;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.api.util.DateConvertUtils;
import com.tduck.cloud.api.util.HttpUtils;
import com.tduck.cloud.api.web.fb.service.OauthService;
import com.tduck.cloud.common.constant.CommonConstants;
import com.tduck.cloud.common.constant.FanbookCard;
import com.tduck.cloud.common.entity.BaseEntity;
import com.tduck.cloud.common.util.JsonUtils;
import com.tduck.cloud.common.util.RedisUtils;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.common.validator.ValidatorUtils;
import com.tduck.cloud.common.validator.group.AddGroup;
import com.tduck.cloud.common.validator.group.UpdateGroup;
import com.tduck.cloud.project.constant.ProjectRedisKeyConstants;
import com.tduck.cloud.project.entity.*;
import com.tduck.cloud.project.entity.enums.ProjectSourceTypeEnum;
import com.tduck.cloud.project.entity.enums.ProjectStatusEnum;
import com.tduck.cloud.project.entity.struct.ItemDefaultValueStruct;
import com.tduck.cloud.project.request.OperateProjectItemRequest;
import com.tduck.cloud.project.request.QueryProjectItemRequest;
import com.tduck.cloud.project.request.QueryProjectRequest;
import com.tduck.cloud.project.request.SortProjectItemRequest;
import com.tduck.cloud.project.service.*;
import com.tduck.cloud.project.util.SortUtils;
import com.tduck.cloud.project.vo.*;
import com.tduck.cloud.wx.mp.constant.WxMpRedisKeyConstants;
import com.tduck.cloud.wx.mp.request.WxMpQrCodeGenRequest;
import com.tduck.cloud.wx.mp.service.WxMpUserService;
import com.tduck.cloud.wx.mp.vo.WxMpUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : smalljop
 * @description : 项目
 * @create : 2020-11-18 18:17
 **/
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserProjectController {

    private final UserProjectService projectService;
    private final UserPublishService userPublishService;
    private final UserProjectItemService projectItemService;
    private final UserProjectResultService projectResultService;
    private final UserService userService;
    private final SortUtils sortUtils;
    private final UserProjectLogicService projectLogicService;
    private final UserProjectThemeService userProjectThemeService;
    private final UserProjectSettingService userProjectSettingService;
    private final ProjectTemplateService projectTemplateService;
    private final ProjectTemplateItemService projectTemplateItemService;
    private final WxMpUserService wxMpUserService;
    private final RedisUtils redisUtils;
    private final FanbookService fanbookService;
    private final WxMpService wxMpService;

    private final ProjectPrizeSettingService projectPrizeSettingService;
    private final ProjectPrizeItemService projectPrizeItemService;
    private final OauthService oauthService;


    @Value("${fb.open.redirect_uri}")
    String appUrl;



    /**
     * 当前设置的状态
     */
    @GetMapping("/user/project/getStatus")
    public Result getStatus(String key) {
        List<UserProjectEntity> list = projectService.lambdaQuery().eq(UserProjectEntity::getKey, key).list();
        return Result.success(list.get(0).getStatus());
    }


    /**
     * 创建项目
     */
    @Login
    @PostMapping("/user/project/create")
    public Result createProject(@RequestBody UserProjectEntity project) {
        ValidatorUtils.validateEntity(project, AddGroup.class);
        UserEntity userEntity = userService.getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getFbUser, project.getFbUser()));
        project.setKey(IdUtil.fastSimpleUUID());
        project.setUserId(userEntity.getId());
        project.setFbUser(project.getFbUser());
        project.setStatus(ProjectStatusEnum.CREATE);
        project.setSourceType(ProjectSourceTypeEnum.BLANK);
        projectService.save(project);
        return Result.success(project.getKey());
    }

    /**
     * 复制项目
     */
    @Login
    @PostMapping("/user/project/copy")
    public Result copyProject(@RequestBody UserProjectEntity project) {
        ValidatorUtils.validateEntity(project, AddGroup.class);
        //为当前登录用户
        UserEntity userEntity = userService.getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getFbUser, project.getFbUser()));
        String oldKey = project.getKey();
        //复制项目
        project.setName(project.getName());
        project.setAnswerNum(0);
        project.setPublishNum(0);
        project.setKey(IdUtil.fastSimpleUUID());
        project.setUserId(userEntity.getId());
        project.setFbUser(project.getFbUser());
        projectService.save(project);
        //复制表单项
        String newKey = project.getKey();
        List<UserProjectItemEntity> userProjectItemEntities = projectItemService.listByProjectKey(oldKey);
        List<UserProjectItemEntity> userProjectItemEntityList = JsonUtils.jsonToList(JsonUtils.objToJson(userProjectItemEntities), UserProjectItemEntity.class);
        userProjectItemEntityList.forEach(item -> item.setProjectKey(newKey));
        projectItemService.saveBatch(userProjectItemEntityList);
        //复制逻辑
        UserProjectLogicEntity logic = projectLogicService.
                getOne(Wrappers.<UserProjectLogicEntity>lambdaQuery().eq(UserProjectLogicEntity::getProjectKey, oldKey));
        if (ObjectUtil.isNotNull(logic)) {
            logic.setProjectKey(newKey);
            projectLogicService.save(logic);
        }
        //复制主题
        UserProjectThemeEntity theme = userProjectThemeService
                .getOne(Wrappers.<UserProjectThemeEntity>lambdaQuery().eq(UserProjectThemeEntity::getProjectKey, oldKey));
        if (ObjectUtil.isNotNull(theme)) {
            theme.setProjectKey(newKey);
            userProjectThemeService.save(theme);
        }
        //复制设置
        UserProjectSettingEntity setting = userProjectSettingService
                .getOne(Wrappers.<UserProjectSettingEntity>lambdaQuery().eq(UserProjectSettingEntity::getProjectKey, oldKey));
        if (ObjectUtil.isNotNull(setting)) {
            setting.setProjectKey(newKey);
            userProjectSettingService.save(setting);
        }
        return Result.success(project.getKey());
    }

    /**
     * 从模板创建项目
     */
    @Login
    @PostMapping("/user/project/use-template/create")
    public Result createProjectByTemplate(@RequestBody ProjectTemplateEntity request) {
        String templateKey = request.getKey();
        ProjectTemplateEntity projectTemplateEntity = projectTemplateService.getByKey(templateKey);
        List<ProjectTemplateItemEntity> projectTemplateItemEntities = projectTemplateItemService.listByTemplateKey(templateKey);
        UserEntity userEntity = userService.getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getFbUser, request.getFbUser()));
        UserProjectEntity userProjectEntity = new UserProjectEntity();
        BeanUtil.copyProperties(projectTemplateEntity, userProjectEntity, UserProjectEntity.Fields.status);
        userProjectEntity.setSourceType(ProjectSourceTypeEnum.TEMPLATE);
        userProjectEntity.setSourceId(projectTemplateEntity.getId().toString());
        userProjectEntity.setKey(IdUtil.fastSimpleUUID());
        userProjectEntity.setUserId(userEntity.getId());
        userProjectEntity.setStatus(ProjectStatusEnum.CREATE);
        projectService.save(userProjectEntity);
        List<UserProjectItemEntity> userProjectItemEntityList = JsonUtils.jsonToList(JsonUtils.objToJson(projectTemplateItemEntities), UserProjectItemEntity.class);
        userProjectItemEntityList.forEach(item -> item.setProjectKey(userProjectEntity.getKey()));
        projectItemService.saveBatch(userProjectItemEntityList);
        return Result.success(userProjectEntity.getKey());
    }


    /**
     * 项目另存为为模板
     *
     * @param request
     * @return
     */
    @Login
    @PostMapping("/user/project/template/save")
    public Result saveAsProjectTemplate(@RequestBody UserProjectEntity request) {
        UserProjectEntity projectEntity = projectService.getByKey(request.getKey());
        List<UserProjectItemEntity> itemEntityList = projectItemService.listByProjectKey(request.getKey());
        ProjectTemplateEntity projectTemplateEntity = new ProjectTemplateEntity();
        BeanUtil.copyProperties(projectEntity, projectTemplateEntity, UserProjectEntity.Fields.status);
        projectTemplateEntity.setKey(IdUtil.fastSimpleUUID());
        projectTemplateEntity.setCategoryId(CommonConstants.ConstantNumber.FOUR.longValue());
        projectTemplateEntity.setFbUser(request.getFbUser());
        projectTemplateService.save(projectTemplateEntity);
        List<ProjectTemplateItemEntity> projectTemplateItemList = JsonUtils.jsonToList(JsonUtils.objToJson(itemEntityList), ProjectTemplateItemEntity.class);
        projectTemplateItemList.forEach(item -> item.setProjectKey(projectTemplateEntity.getKey()));
        projectTemplateItemService.saveBatch(projectTemplateItemList);
        return Result.success(projectTemplateEntity.getKey());
    }


    /**
     * 根据条件查询所有项目
     */
    @Login
    @GetMapping("/user/project/list")
    public Result listProjects(QueryProjectRequest.List request, @RequestAttribute Long fbUser) {
        List<UserProjectEntity> entityList = projectService.list(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getFbUser, fbUser)
                .eq(ObjectUtil.isNotNull(request.getStatus()), UserProjectEntity::getStatus, request.getStatus())
                .orderByDesc(BaseEntity::getUpdateTime));
        return Result.success(entityList);
    }

    /**
     * 查询我的项目分页
     */
    @Login
    @GetMapping("/user/project/page")
    public Result queryMyProjects(QueryProjectRequest.Page request) {
        return Result.success(projectService.page(request.toMybatisPage(),
                Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getFbUser, request.getFbUser())
                        .eq(UserProjectEntity::getDeleted, false)
                        .eq(ObjectUtil.isNotNull(request.getStatus()), UserProjectEntity::getStatus, request.getStatus())
                        .like(StrUtil.isNotBlank(request.getName()), UserProjectEntity::getName, request.getName())
                        .le(ObjectUtil.isNotNull(request.getEndDateTime()), UserProjectEntity::getUpdateTime, request.getEndDateTime())
                        .ge(ObjectUtil.isNotNull(request.getBeginDateTime()), UserProjectEntity::getUpdateTime, request.getBeginDateTime())
                        .orderByDesc(BaseEntity::getCreateTime)));
    }


    /**
     * 查询项目
     */
    @GetMapping("/user/project/{key}")
    public Result queryProjectByKey(@PathVariable @NotBlank String key) {
        return Result.success(projectService.getByKey(key));
    }


    /**
     * 发布项目
     */
    @Login
    @PostMapping("/user/project/publish")
    public Result publishProject(@RequestBody UserProjectEntity request) {
        int count = projectItemService
                .count(Wrappers.<UserProjectItemEntity>lambdaQuery().eq(UserProjectItemEntity::getProjectKey, request.getKey()));
        if (count == CommonConstants.ConstantNumber.ZERO) {
            return Result.failed("请先编辑在发布");
        }
        UserProjectEntity entity = projectService.getByKey(request.getKey());
        if (entity.getStatus() == ProjectStatusEnum.RELEASE) {
            return Result.failed("该问卷已发布");
        }
        entity.setStatus(ProjectStatusEnum.RELEASE);

        Integer publishNum = entity.getPublishNum();
        publishNum++;
        entity.setPublishNum(publishNum);
//        if (request.getPublishList().size() > 0) {
//            List<PublishEntity> publishList = request.getPublishList();
//            publishList.forEach(pe -> {
//                if (!StringUtils.isEmpty(pe.getFbChannel())) {
//                    PublishEntity pes = userPublishService.getOne((Wrappers.<PublishEntity>lambdaQuery()
//                            .eq(PublishEntity::getKey, request.getKey())
//                            .eq(PublishEntity::getGuildId, pe.getGuildId())
//                            .eq(PublishEntity::getFbChannel, pe.getFbChannel())
//                    ));
//                    if (null == pes) {
//                        PublishEntity publishEntity = PublishEntity.builder()
//                                .key(request.getKey())
//                                .guildName(pe.getGuildName())
//                                .guildId(pe.getGuildId())
//                                .fbChannelName(pe.getFbChannelName())
//                                .fbChannel(pe.getFbChannel())
//                                .build();
//                        userPublishService.save(publishEntity);
//                    }
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("chat_id", pe.getFbChannel());
//                    Document doc = Jsoup.parse(entity.getDescribe());
//                    Elements links = doc.getElementsByTag("p");
//                    JSONObject cardJson = FanbookCard.getWenJuanString(entity.getName(), links.get(0).text(), appUrl + "s/" + request.getKey());
//                    JSONObject taskJson = new JSONObject();
//                    taskJson.put("type", "task");
//                    taskJson.put("content", cardJson);
//                    jsonObject.put("text", taskJson.toString());
//                    jsonObject.put("parse_mode", "Fanbook");
//                    String rstr = fanbookService.sendMessage(jsonObject);
//                    log.debug("发送文件返回：" + rstr);
//                }
//            });
//        }
        boolean update = projectService.updateById(entity);
        return Result.success(entity, "发布"+update);
    }

    /**
     * 推送消息
     */
    @Login
    @GetMapping("/user/project/refreshMsg")
    public Result refreshMsg(@RequestParam("key") String key, @RequestParam("fbChannel") String fbChannel) {
        String msg="";
        UserProjectEntity entity = projectService.getByKey(key);
        if (!StringUtils.isEmpty(fbChannel)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chat_id", Long.valueOf(fbChannel));
            Document doc = Jsoup.parse(entity.getDescribe());
            Elements links = doc.getElementsByTag("p");
            JSONObject cardJson = FanbookCard.getWenJuanString(entity.getName(), links.get(0).text(), appUrl + "s/" + key);
            JSONObject taskJson = new JSONObject();
            taskJson.put("type", "task");
            taskJson.put("content", cardJson);
            jsonObject.put("text", taskJson.toString());
            jsonObject.put("parse_mode", "Fanbook");
            System.out.println(jsonObject);
            String rstr = fanbookService.sendMessage(jsonObject);

        }
        return Result.success();
    }

    /**
     * 推送消息
     */
    @Login
    @PostMapping("/user/project/publishMsg")
    public Result publishMsg(@RequestBody UserProjectEntity request) {
        UserProjectEntity entity = projectService.getByKey(request.getKey());
//        PublishEntity publishEntity = PublishEntity.builder()
//                .key(request.getKey())
//                .guildName(request.getGuildName())
//                .guildId(request.getGuildId())
//                .fbChannelName(request.getFbChannelName())
//                .fbChannel(request.getFbChannel())
//                .status(request.getStatus())
//                .build();
        if (request.getPublishList().size() > 0) {
            request.getPublishList().forEach(obj -> {


                if (!StringUtils.isEmpty(obj.getFbChannel())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("chat_id", obj.getFbChannel());
                    Document doc = Jsoup.parse(entity.getDescribe());
                    Elements links = doc.getElementsByTag("p");
                    JSONObject cardJson = FanbookCard.getWenJuanString(entity.getName(), links.get(0).text(), appUrl + "s/" + request.getKey());
                    JSONObject taskJson = new JSONObject();
                    taskJson.put("type", "task");
                    taskJson.put("content", cardJson);
                    jsonObject.put("text", taskJson.toString());
                    jsonObject.put("parse_mode", "Fanbook");
                    String rstr = fanbookService.sendMessage(jsonObject);

                    Boolean aBoolean=(Boolean)JSONObject.parseObject(rstr).get("ok");
                    log.debug("发送文件返回：" + rstr);
                    //是否已经推送
                    Integer count = userPublishService.count(Wrappers.<PublishEntity>lambdaQuery()
                            .eq(PublishEntity::getKey, request.getKey())
                            .eq(PublishEntity::getFbChannel, obj.getFbChannel().toString()));
                    obj.setKey(request.getKey());
                    obj.setStatus(aBoolean ? 1 : 2);
                    if (count == 0) {
                        obj.setStatus(aBoolean ? 1 : 2);
                        userPublishService.save(obj);
                    } else {
                        String timeStr1 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
                        userPublishService.update(Wrappers.<PublishEntity>lambdaUpdate().set(PublishEntity::getStatus,aBoolean ? 1 : 2).set(PublishEntity::getUpdateTime, timeStr1).eq(PublishEntity::getKey, request.getKey()).eq(PublishEntity::getFbChannel, obj.getFbChannel()));
                    }
                    log.debug("发送文件返回：" + rstr);
                }
            });
        }
        return Result.success();
    }

    /**
     * 推送消息(定时)
     */
    @Login
    @PostMapping("/user/project/timingPublishMsg")
    public Result timingPublishMsg(@RequestBody UserProjectEntity request) throws Exception{
        UserProjectEntity entity = projectService.getByKey(request.getKey());

//        Date parse = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(request.getPublishTime());
        Date parse = StrUtil.isNotBlank(request.getPublishTime()) ? new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(request.getPublishTime()) : new Date(System.currentTimeMillis());
        Timer timer = new Timer();

        if (request.getPublishList().size() > 0) {
            request.getPublishList().forEach(obj -> {

                if (!StringUtils.isEmpty(obj.getFbChannel())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("chat_id", obj.getFbChannel());
                    Document doc = Jsoup.parse(entity.getDescribe());
                    Elements links = doc.getElementsByTag("p");
                    ValidatorUtils.validateEntity(links);
                    JSONObject cardJson = FanbookCard.getWenJuanString(entity.getName(), links.get(0).text(), appUrl + "s/" + request.getKey());
                    JSONObject taskJson = new JSONObject();
                    taskJson.put("type", "task");
                    taskJson.put("content", cardJson);
                    jsonObject.put("text", taskJson.toString());
                    jsonObject.put("parse_mode", "Fanbook");
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            String rstr = fanbookService.sendMessage(jsonObject);
                            Boolean aBoolean=(Boolean)JSONObject.parseObject(rstr).get("ok");
                            obj.setStatus(aBoolean ? 1 : 2);
                            userPublishService.updateById(obj);
                            if ((aBoolean ? 1 : 2) == 1) {
                                entity.setPublishNum((int) redisUtils.incr(StrUtil.format(ProjectRedisKeyConstants.PROJECT_PUBLISH_NUMBER, entity.getKey()), CommonConstants.ConstantNumber.ONE));
                                projectService.updateById(entity);
                            }
                            log.debug("发送文件返回：" + rstr);
                        }
                    }, parse);

                    obj.setKey(request.getKey());
                    obj.setStatus(3);
                    obj.setPublishTime(DateConvertUtils.dateToLocalDateTime(parse));
                    userPublishService.save(obj);
                }
            });
        }
        return Result.success();
    }

    /**
     * 获取用户推送消息列表
     */
    @Login
    @GetMapping("/user/project/getPublishList")
    public Result getPublishList(@RequestParam("key") String key) {
        List<PublishEntity> list = userPublishService.list(Wrappers.<PublishEntity>lambdaQuery().eq(PublishEntity::getKey, key));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", key);
        jsonObject.put("publishList", list);
        return Result.success(jsonObject);
    }

    /**
     * 停止收集
     *
     * @param request
     */
    @Login
    @PostMapping("/user/project/stop")
    public Result stopProject(@RequestBody UserProjectEntity request) {
        /**
         * 抽奖
         * */

        List<ProjectPrizeSettingEntity> settingList = projectPrizeSettingService.lambdaQuery().eq(ProjectPrizeSettingEntity::getProjectKey,request.getKey()).list();
        if(settingList.size() > 0) {
            ProjectPrizeSettingEntity setting = settingList.get(0);

            if (setting.getType() != 1) {
                ///问卷停止后发放
                List<UserProjectResultEntity> list = projectResultService.lambdaQuery().eq(UserProjectResultEntity::getProjectKey,request.getKey()).list();

                for (UserProjectResultEntity item : list){
                    if(winPrize(setting,request.getKey(),item) == false){
                        break;
                    }
                }
            }
        }
        //抽奖 end

        UserProjectEntity entity = projectService.getByKey(request.getKey());
        entity.setStatus(ProjectStatusEnum.STOP);
        return Result.success(projectService.updateById(entity));
    }

    private Boolean winPrize(ProjectPrizeSettingEntity setting,String projectKey,UserProjectResultEntity userProjectResultEntity){
        if(setting.getProbability() == 1 || (int)Math.random()*(setting.getProbability()+1) == 1)
        {
            //中奖逻辑
            List<ProjectPrizeItemEntity> prizeList = projectPrizeItemService.lambdaQuery()
                    .eq(ProjectPrizeItemEntity::getProjectKey,projectKey)
                    .eq(ProjectPrizeItemEntity::getFanbookid,"")
                    .eq(ProjectPrizeItemEntity::getStatus,true).list();

            if(prizeList.size() > 0)
            {
                UserEntity userEntity = userService.getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getFbUser,userProjectResultEntity.getFbUserid()));

                //可以中奖
                ProjectPrizeItemEntity prizeItem = prizeList.get(0);
                ProjectPrizeItemEntity newItem = ProjectPrizeItemEntity.builder()
                        .phoneNumber(userEntity.getPhoneNumber())
                        .fanbookid(userEntity.getFbUser())
                        .nickname(userEntity.getName())
                        .getTime(LocalDateTime.now())
                        .build();

                Boolean result = projectPrizeItemService.lambdaUpdate().eq(ProjectPrizeItemEntity::getId,prizeItem.getId())
                        .update(newItem);

                if(result) {
                    if (prizeItem.getType() == 1) {

                        ThreadUtil.execAsync(() -> {
                            //添加积分
                            oauthService.modifyUserPoint(prizeItem.getId() + "", Long.valueOf(userProjectResultEntity.getGuildId()), Long.valueOf(userProjectResultEntity.getFbUserid()), Integer.valueOf(prizeItem.getPrize()), "奖励积分");
                        });
                    }
                }
                return true;
            }else{
                //奖品不足 停止发奖
                return false;
            }
        }else{
            return true;
        }
    }

    /**
     * 删除项目
     *
     * @param request
     */
    @Login
    @PostMapping("/user/project/delete")
    public Result deleteProject(@RequestBody UserProjectEntity request) {
        boolean del = projectService.update(
                new UserProjectEntity() {{
                    setDeleted(Boolean.TRUE);
                }},
                Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, request.getKey()));
        return Result.success(del);
    }


    /**
     * 查询项目详情
     * 包含项目信息 项目保单项信息 项目主题
     *
     * @param key
     */
    @GetMapping("/user/project/details/{key}")
    public Result queryProjectDetails(@PathVariable @NotBlank String key) {

        //获取配置时间
        UserProjectSettingEntity entity = userProjectSettingService
                .getOne(Wrappers.<UserProjectSettingEntity>lambdaQuery().eq(UserProjectSettingEntity::getProjectKey, key));

        if (ObjectUtil.isNotNull(entity)) {
            if(entity.getStartTime() != null && LocalDateTime.now().isBefore(LocalDateTime.parse(entity.getStartTime().toString()))){
                return Result.failed("问卷还未开始");
            }

            if(entity.getEndTime() != null && LocalDateTime.now().isAfter(LocalDateTime.parse(entity.getEndTime().toString()))){
                return Result.failed("问卷已经结束");
            }
        }

        UserProjectEntity project = projectService.getByKey(key);
        List<UserProjectItemEntity> projectItemList = projectItemService.listByProjectKey(key);
        UserProjectThemeVo themeVo = userProjectThemeService.getUserProjectDetails(key);
        return Result.success(new UserProjectDetailVO(project, projectItemList, themeVo));
    }


    /**
     * 项目更新
     *
     * @param project
     *
     */
    @Login
    @PostMapping("/user/project/update")
    public Result updateProject(@RequestBody UserProjectEntity project) {
        ValidatorUtils.validateEntity(project, AddGroup.class);
        UserProjectEntity oldProject = projectService.getByKey(project.getKey());
        if (ObjectUtil.isNotNull(oldProject) && project.getFbUser().equals(oldProject.getFbUser())) {
            project.setId(oldProject.getId());
            projectService.updateById(project);
        }
        return Result.success();
    }

    /**
     * 表单项最大Id
     */
    @Login
    @GetMapping("/user/project/item/max-form-id")
    public Result queryProjectMaxFormItemId(@RequestParam @NotBlank String key) {
        UserProjectItemEntity entity = projectItemService.getOne(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, key).select().orderByDesc(UserProjectItemEntity::getFormItemId).last("limit 1"));
        return Result.success(ObjectUtil.isNotNull(entity) ? entity.getFormItemId() : null);
    }

    /**
     * 项目表单项查询
     */
    @GetMapping("/user/project/item/list")
    public Result queryProjectItem(QueryProjectItemRequest request) {
        ValidatorUtils.validateEntity(request);
        List<UserProjectItemEntity> itemEntityList = projectItemService.list(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, request.getKey())
                .eq(ObjectUtil.isNotNull(request.getDisplayType()), UserProjectItemEntity::getDisplayType, request.getDisplayType())
                .orderByAsc(UserProjectItemEntity::getSort)
        );
        return Result.success(itemEntityList);
    }


    /**
     * 项目表单项创建
     *
     * @param request
     */
    @Login
    @PostMapping("/user/project/item/create")
    public Result createProjectItem(@RequestBody OperateProjectItemRequest request) {
        ValidatorUtils.validateEntity(request, AddGroup.class);
        UserProjectItemEntity entity = formatProjectItem(request);
        //排序下标计算
        entity.setSort(sortUtils.getInitialSortPosition(
                StrUtil.format(ProjectRedisKeyConstants.PROJECT_ITEM_POS_DELTA, request.getProjectKey())));
        boolean save = projectItemService.save(entity);

        return Result.success(new OperateProjectItemVO(entity.getSort(), entity.getId(), save));
    }
    @Login
    @PostMapping("/user/project/item/updateProjectName")
    public Result updateProjectName(String name,String Key){
        String timeStr1 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        boolean update = projectService.update(Wrappers.<UserProjectEntity>lambdaUpdate().set(UserProjectEntity::getName, name).set(UserProjectEntity::getUpdateTime, timeStr1).eq(UserProjectEntity::getKey, Key));
        if(!update){
            return Result.restResult(null,200,"修改失败！");
        }
        return Result.success();
    }

    /**
     * 格式化项目Item的数据
     */
    private UserProjectItemEntity formatProjectItem(OperateProjectItemRequest request) {
        //把Map转换成Bean 在转换成Map 去除不在bean字段列表的多字段
        Object bean = BeanUtil.toBeanIgnoreCase(request.getExpand(), request.getType().getExpandClass(), false);
        UserProjectItemEntity entity = new UserProjectItemEntity();
        BeanUtil.copyProperties(request, entity, UserProjectItemEntity.Fields.defaultValue);
        entity.setExpand(BeanUtil.beanToMap(bean));
        //默认值格式化 1判断是否是Json
        Object defaultValue = request.getDefaultValue();
        if (ObjectUtil.isNotEmpty(defaultValue)) {
            boolean json = JSONUtil.isJson(JsonUtils.objToJson(request.getDefaultValue()));
            if (json) {
                entity.setDefaultValue(new ItemDefaultValueStruct(true, JsonUtils.objToJson(request.getDefaultValue())));
            }
        }
        entity.setDefaultValue(new ItemDefaultValueStruct(false, defaultValue));
        return entity;
    }


    /**
     * 表单项更新
     *
     * @param request
     */
    @Login
    @PostMapping("/user/project/item/update")
    public Result updateProjectItem(@RequestBody OperateProjectItemRequest request) {
        ValidatorUtils.validateEntity(request, UpdateGroup.class);
        boolean update = projectItemService.update(formatProjectItem(request), Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, request.getProjectKey())
                .eq(UserProjectItemEntity::getFormItemId, request.getFormItemId()));
        return Result.success(update);
    }


    /**
     * 表单项删除
     */
    @Login
    @PostMapping("/user/project/item/delete")
    public Result deleteProjectItem(@RequestBody OperateProjectItemRequest request) {
        ValidatorUtils.validateEntity(request, OperateProjectItemRequest.DeleteGroup.class);
        boolean delete = projectItemService.remove(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, request.getProjectKey())
                .eq(UserProjectItemEntity::getFormItemId, request.getFormItemId())
                .eq(UserProjectItemEntity::getType, request.getType()));
        return Result.success(delete);
    }

    /**
     * 表单项排序
     *
     * @param request
     */
    @Login
    @PostMapping("/user/project/item/sort")
    public Result sortProjectItem(@RequestBody SortProjectItemRequest request) {
        ValidatorUtils.validateEntity(request);
        if (ObjectUtil.isNull(request.getAfterPosition())
                && ObjectUtil.isNull(request.getBeforePosition())) {
            return Result.success();
        }
        UserProjectItemEntity itemEntity = projectItemService.getOne(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, request.getProjectKey())
                .eq(UserProjectItemEntity::getFormItemId, request.getFormItemId()));
        Long sort = sortUtils.calcSortPosition(request.getBeforePosition(), request.getAfterPosition());
        itemEntity.setSort(sort);
        boolean b = projectItemService.updateById(itemEntity);
        return Result.success(new OperateProjectItemVO(itemEntity.getSort(), itemEntity.getId(), b));
    }


    /**
     * 项目主题保存
     *
     * @param themeEntity
     */
    @Login
    @PostMapping("/user/project/theme/save")
    public Result saveProjectTheme(@RequestBody UserProjectThemeEntity themeEntity) {
        ValidatorUtils.validateEntity(themeEntity);
        UserProjectThemeEntity entity = userProjectThemeService
                .getOne(Wrappers.<UserProjectThemeEntity>lambdaQuery().eq(UserProjectThemeEntity::getProjectKey, themeEntity.getProjectKey()));
        if (ObjectUtil.isNotNull(entity)) {
            themeEntity.setId(entity.getId());
        }
        return Result.success(userProjectThemeService.saveOrUpdate(themeEntity));
    }


    /**
     * 项目主题查询
     *
     * @param projectKey
     */
    @Login
    @GetMapping("/user/project/theme/{key}")
    public Result queryThemeByKey(@PathVariable("key") String projectKey) {
        UserProjectThemeEntity entity = userProjectThemeService
                .getOne(Wrappers.<UserProjectThemeEntity>lambdaQuery().eq(UserProjectThemeEntity::getProjectKey, projectKey));
        return Result.success(entity);
    }

    /**
     * 项目设置保存
     *
     * @param settingEntity
     */
    @Login
    @PostMapping("/user/project/setting/save")
    public Result saveProjectSetting(@RequestBody UserProjectSettingEntity settingEntity) {
        ValidatorUtils.validateEntity(settingEntity);
        UserProjectSettingEntity entity = userProjectSettingService
                .getOne(Wrappers.<UserProjectSettingEntity>lambdaQuery().eq(UserProjectSettingEntity::getProjectKey, settingEntity.getProjectKey()));
        if (ObjectUtil.isNotNull(entity)) {
            settingEntity.setId(entity.getId());
        }
        return Result.success(userProjectSettingService.saveOrUpdate(settingEntity));
    }


    /**
     * 项目设置查询
     *
     * @param projectKey
     */
    @GetMapping("/user/project/setting/{key}")
    public Result querySettingByKey(@PathVariable("key") String projectKey) {
        UserProjectSettingEntity entity = userProjectSettingService
                .getOne(Wrappers.<UserProjectSettingEntity>lambdaQuery().eq(UserProjectSettingEntity::getProjectKey, projectKey));
        return Result.success(entity);
    }


    /**
     * 当前设置的状态
     */
    @GetMapping("/user/project/setting-status")
    public Result querySettingStatus(@RequestParam String projectKey, @RequestParam(required = false) String wxOpenId, HttpServletRequest request) {
        return userProjectSettingService.getUserProjectSettingStatus(projectKey, HttpUtils.getIpAddr(request), wxOpenId);
    }


    /**
     * 填写微信通知二维码
     */
    @GetMapping("/user/project/wx/notify-qrcode")
    public Result getWxNotifyQrCode(@RequestParam("key") String projectKey) throws WxErrorException {
        String loginSceneStr = JsonUtils.objToJson(new WxMpQrCodeGenRequest(WxMpQrCodeGenRequest.QrCodeType.SUB_NOTIFY, projectKey));
        //5分钟有效
//        WxMpQrCodeTicket ticket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(loginSceneStr, 10 * 60);
//        String subNotifyQrcodeUrl = wxMpService.getQrcodeService().qrCodePictureUrl(ticket.getTicket());
//        return Result.success(subNotifyQrcodeUrl);
        return Result.success();
    }


    /**
     * 填写微信通知二维码
     */
    @PostMapping("/user/project/wx/delete/notify-user")
    public Result deleteWxNotifyQrCode(@RequestParam("key") String key, @RequestParam("openId") String openId) {
        redisUtils.setRemove(StrUtil.format(WxMpRedisKeyConstants.WX_MP_SUB_NOTIFY, key), openId);
        return Result.success();
    }

    /**
     * 获取项目微信通知用户
     */
    @GetMapping("/user/project/wx/notify-user")
    public Result getWxNotifyUser(@RequestParam("key") String projectKey, @RequestParam(required = false) String openIdStr) {
        Set<Object> subNotifyUsers = null;
        if (StrUtil.isNotBlank(openIdStr)) {
            subNotifyUsers = Sets.newHashSet(StrUtil.splitTrim(openIdStr, ";"));
        } else {
            subNotifyUsers = redisUtils.setMembers(StrUtil.format(WxMpRedisKeyConstants.WX_MP_SUB_NOTIFY, projectKey));
        }
        return Result.success(wxMpUserService.listWxMpUserByOpenId(subNotifyUsers)
                .stream().map(item -> new WxMpUserVO(item.getNickname(), item.getHeadImgUrl(), item.getOpenId())).collect(Collectors.toList()));
    }

    /**
     * 回收站项目分页
     */
    @Login
    @GetMapping("/user/project/recycle/page")
    public Result queryRecycleProjects(QueryProjectRequest.Page request) {
        Page page = projectService.page(request.toMybatisPage(),
                Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getFbUser, request.getFbUser())
                        .eq(UserProjectEntity::getDeleted, true)
                        .orderByDesc(BaseEntity::getUpdateTime));
        List<UserProjectEntity> records = page.getRecords();
        List<RecycleProjectVO> projectVOList = records.stream().map(item -> {
            int count = projectResultService.count(Wrappers.<UserProjectResultEntity>lambdaQuery().eq(UserProjectResultEntity::getProjectKey, item.getKey()));
            return new RecycleProjectVO(item.getKey(), count, item.getName(), item.getCreateTime(), item.getUpdateTime());
        }).collect(Collectors.toList());
        page.setRecords(projectVOList);
        return Result.success(page);
    }

    /**
     * 从回收站中恢复项目
     */
    @Login
    @PostMapping("/user/project/recycle/restore")
    public Result restoreRecycleProject(@RequestBody UserProjectEntity request) {
        boolean flag = projectService.update(
                new UserProjectEntity() {{
                    setDeleted(Boolean.FALSE);
                }},
                Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, request.getKey()));
        return Result.success(flag);
    }

    /**
     * 从回收站中删除项目
     */
    @Login
    @PostMapping("/user/project/recycle/delete")
    public Result deleteRecycleProject(@RequestBody UserProjectEntity projectEntity) {
        boolean remove = projectService.remove(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getFbUser, projectEntity.getFbUser())
                .eq(ObjectUtil.isNotNull(projectEntity.getKey()), UserProjectEntity::getKey, projectEntity.getKey())
                .eq(ObjectUtil.isNotNull(projectEntity.getDeleted()), UserProjectEntity::getDeleted, projectEntity.getDeleted()));

        projectItemService.remove(Wrappers.<UserProjectItemEntity>lambdaQuery()
                .eq(UserProjectItemEntity::getProjectKey, projectEntity.getKey()));
        projectLogicService.remove(Wrappers.<UserProjectLogicEntity>lambdaQuery()
                .eq(UserProjectLogicEntity::getProjectKey, projectEntity.getKey()));
        userProjectThemeService.remove(Wrappers.<UserProjectThemeEntity>lambdaQuery()
                .eq(UserProjectThemeEntity::getProjectKey, projectEntity.getKey()));
        userProjectSettingService.remove(Wrappers.<UserProjectSettingEntity>lambdaQuery()
                .eq(UserProjectSettingEntity::getProjectKey, projectEntity.getKey()));

        return Result.success(remove);
    }

    /**
     * 清空回收站
     */
    @Login
    @PostMapping("/user/project/recycle/clear")
    public Result clearRecycleProject(@RequestBody UserProjectEntity projectEntity) {

        List<UserProjectEntity> list = projectService.list(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getFbUser, projectEntity.getFbUser())
                .eq(UserProjectEntity::getDeleted, projectEntity.getDeleted()));
        List<String> keys = list.stream().map(UserProjectEntity::getKey).collect(Collectors.toList());

        int key = projectService.getBaseMapper().delete(new QueryWrapper<UserProjectEntity>().in("key", keys));

        projectItemService.getBaseMapper().delete(new QueryWrapper<UserProjectItemEntity>().in("project_Key", keys));
        projectLogicService.getBaseMapper().delete(new QueryWrapper<UserProjectLogicEntity>().in("project_Key", keys));
        userProjectThemeService.getBaseMapper().delete(new QueryWrapper<UserProjectThemeEntity>().in("project_Key", keys));
        userProjectSettingService.getBaseMapper().delete(new QueryWrapper<UserProjectSettingEntity>().in("project_Key", keys));

        return Result.success(key);
    }



}
