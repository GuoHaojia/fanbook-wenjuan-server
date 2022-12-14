package com.tduck.cloud.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tduck.cloud.common.constant.CommonConstants;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.project.entity.UserProjectEntity;
import com.tduck.cloud.project.entity.UserProjectResultEntity;
import com.tduck.cloud.project.entity.UserProjectSettingEntity;
import com.tduck.cloud.project.entity.enums.ProjectStatusEnum;
import com.tduck.cloud.project.mapper.UserProjectSettingMapper;
import com.tduck.cloud.project.service.UserProjectResultService;
import com.tduck.cloud.project.service.UserProjectService;
import com.tduck.cloud.project.service.UserProjectSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * 项目表单项(UserProjectSetting)表服务实现类
 *
 * @author smalljop
 * @since 2020-11-30 14:00:53
 */
@Service("userProjectSettingService")
@RequiredArgsConstructor
public class UserProjectSettingServiceImpl extends ServiceImpl<UserProjectSettingMapper, UserProjectSettingEntity> implements UserProjectSettingService {

    private final UserProjectResultService userProjectResultService;
    private final UserProjectService userProjectService;


    @Override
    public Result getUserProjectSettingStatus(String projectKey, String requestIp, Long wxOpenId) {
        UserProjectEntity userProjectEntity = userProjectService.getByKey(projectKey);
        if (ObjectUtil.isNull(userProjectEntity) || userProjectEntity.getStatus() != ProjectStatusEnum.RELEASE) {
            return Result.success(null, "项目暂时无法填写");
        }

        UserProjectSettingEntity setting = this
                .getOne(Wrappers.<UserProjectSettingEntity>lambdaQuery().eq(UserProjectSettingEntity::getProjectKey, projectKey));
        if (ObjectUtil.isNull(setting)) {
            return Result.success(setting);
        }

        Optional<LocalDateTime> timedCollectionBeginTime = Optional.ofNullable(setting.getStartTime());
        Optional<LocalDateTime> timedCollectionEndTime = Optional.ofNullable(setting.getEndTime());
        LocalDateTime now = LocalDateTime.now();
        //时间未开始
        if (timedCollectionBeginTime.isPresent() && timedCollectionBeginTime.get().isAfter(now)) {
            return Result.success(null, StringUtils.isEmpty(setting.getTimedNotEnabledPromptText()) ? "项目时间末开始。" : setting.getTimedNotEnabledPromptText());
        }

        //时间已经结束
        if (timedCollectionEndTime.isPresent() && timedCollectionEndTime.get().isBefore(now)) {
            return Result.success(null, StringUtils.isEmpty(setting.getTimedDeactivatePromptText()) ? "项目时间已结束。" : setting.getTimedDeactivatePromptText());
        }

        //收集数量达到
        //Integer timedQuantitativeQuantity = setting.getTimedQuantitativeQuantity();
        //if (Optional.ofNullable(timedQuantitativeQuantity).isPresent() && 0 != timedQuantitativeQuantity) {
        //    int resultCount = userProjectResultService.count(Wrappers.<UserProjectResultEntity>lambdaQuery().eq(UserProjectResultEntity::getProjectKey, projectKey));
        //    if (resultCount >= timedQuantitativeQuantity) {
        //        return Result.success(setting, StringUtils.isEmpty(setting.getTimedEndPromptText()) ? "收集数量已达到。" : setting.getTimedEndPromptText());
        //    }
        //}

        //每个人只需填写一次 根据IP判断
        Integer everyoneWriteOnce = setting.getEveryoneWriteOnce();
        //Boolean everyoneDayWriteOnce = setting.getEveryoneDayWriteOnce();
        if (everyoneWriteOnce > 0) {
            LambdaQueryWrapper<UserProjectResultEntity> wrapper = new LambdaQueryWrapper<UserProjectResultEntity>();
            if(wxOpenId!=null){
                //fanbook填写
                wrapper = Wrappers.<UserProjectResultEntity>lambdaQuery()
                        .eq(UserProjectResultEntity::getProjectKey, projectKey)
                        .eq(UserProjectResultEntity::getFbUserid, wxOpenId);
            }else{
                //非fanbook填写 ip过滤
                wrapper = Wrappers.<UserProjectResultEntity>lambdaQuery()
                        .eq(UserProjectResultEntity::getProjectKey, projectKey)
                        .eq(UserProjectResultEntity::getSubmitRequestIp, requestIp);
            }

            /*if (everyoneDayWriteOnce) {
                wrapper.apply(StrUtil.format("date_format({},'%Y-%m-%d') = '{}'",
                        StrUtil.toUnderlineCase(BaseEntity.Fields.createTime), DateUtil.today()));
            }*/
            int writeCount = userProjectResultService.count(wrapper);

            //if (CommonConstants.ConstantNumber.ONE <= writeCount) {
            if (everyoneWriteOnce <= writeCount) {
                Logger.getLogger("问卷限制").info("projectKey:"+projectKey);
                Logger.getLogger("问卷限制").info("requestIp:"+requestIp);
                Logger.getLogger("问卷限制").info("fbuserid:"+wxOpenId);
                return Result.success(null, "超过填写次数限制");
            }
        }

        //每个fanbookid仅填写一次
        if (setting.getWxWriteOnce() !=null && wxOpenId!=null && setting.getWxWriteOnce() && wxOpenId > 0) {
            LambdaQueryWrapper<UserProjectResultEntity> wrapper = Wrappers.<UserProjectResultEntity>lambdaQuery()
                    .eq(UserProjectResultEntity::getProjectKey, projectKey)
                    .eq(UserProjectResultEntity::getFbUserid, wxOpenId);
            int writeCount = userProjectResultService.count(wrapper);
            if (CommonConstants.ConstantNumber.ONE <= writeCount) {
                Logger.getLogger("问卷已经填写限制").info("projectKey:"+projectKey);
                Logger.getLogger("问卷已经填写限制").info("requestIp:"+requestIp);
                Logger.getLogger("问卷已经填写限制").info("fbuserid:"+wxOpenId);
                return Result.success(null, "已经填写过，无法再次填写");
            }
        }
        return Result.success(setting);
    }

    @Override
    public UserProjectSettingEntity getByProjectKey(String projectKey) {
        return this
                .getOne(Wrappers.<UserProjectSettingEntity>lambdaQuery().eq(UserProjectSettingEntity::getProjectKey, projectKey));
    }
}