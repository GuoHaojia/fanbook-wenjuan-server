package com.tduck.cloud.api.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tduck.cloud.project.entity.UserProjectEntity;
import com.tduck.cloud.project.entity.UserProjectSettingEntity;
import com.tduck.cloud.project.entity.enums.ProjectStatusEnum;
import com.tduck.cloud.project.service.UserProjectService;
import com.tduck.cloud.project.service.UserProjectSettingService;
import com.tduck.cloud.project.util.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TimingTaskUtils {

    private final UserProjectService projectService;
    private final UserProjectSettingService userProjectSettingService;

    @Value("${platform.front.baseUrl}")
    String host;
    @Value("${server.servlet.context-path}")
    String path;

    @Scheduled(cron = "*/2 * * * * ?")
    public void stopQuestionnaire () {
        long start = System.currentTimeMillis();
        System.out.println("查询问卷设置开始");

        List<UserProjectSettingEntity> settings = userProjectSettingService.list(Wrappers.<UserProjectSettingEntity>lambdaQuery().isNotNull(UserProjectSettingEntity::getEndTime));
        Map<String, LocalDateTime> map = settings.stream().collect(Collectors.toMap(UserProjectSettingEntity::getProjectKey, UserProjectSettingEntity::getEndTime));
        map.forEach((k, v) -> {
            UserProjectEntity upe = projectService.getOne(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, k));
            if (ObjectUtil.isNotNull(upe) && upe.getStatus() != ProjectStatusEnum.STOP) {
                LocalDateTime now = LocalDateTime.now();
                if (v.isEqual(now) ||v.isBefore(now)) {
                    String url = host + path + "/user/project/stop";
                    JSONObject request = new JSONObject();
                    request.put("key", upe.getKey());
                    String post = RestTemplateUtil.builder().post(url, request);
                    log.debug("发送请求：" + post);
                }
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("查询结束："+(end-start));
    }
}
