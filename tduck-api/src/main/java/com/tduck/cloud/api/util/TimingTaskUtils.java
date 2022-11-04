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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
//@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TimingTaskUtils {

    private final UserProjectService projectService;
    private final UserProjectSettingService userProjectSettingService;
    private static ConcurrentHashMap<String, LocalDateTime> setMap = new ConcurrentHashMap();

    @Value("${platform.front.baseUrl}")
    String host;
    @Value("${server.servlet.context-path}")
    String path;

    @Scheduled(cron = "*/2 * * * * ?")
    public void stopQuestionnaire () {
        long start = System.currentTimeMillis();
        log.info("查询问卷设置");

        List<UserProjectSettingEntity> settings = userProjectSettingService.list(Wrappers.<UserProjectSettingEntity>lambdaQuery().isNotNull(UserProjectSettingEntity::getEndTime));
        Map<String, LocalDateTime> map = settings.stream().collect(Collectors.toMap(UserProjectSettingEntity::getProjectKey, UserProjectSettingEntity::getEndTime));
        map.forEach((k, v) -> {
            if (!setMap.containsKey(k)) {
                UserProjectEntity upe = projectService.getOne(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, k));
                if (ObjectUtil.isNotNull(upe) && upe.getStatus() != ProjectStatusEnum.STOP) {
                    LocalDateTime now = LocalDateTime.now();
                    if (v.isEqual(now) ||v.isBefore(now)) {
                        String url = host + path + "/user/project/stop";
                        JSONObject request = new JSONObject();
                        request.put("key", upe.getKey());
                        String fbtoken = "mIOHt38z2kZCMlRqIOLY6g==";
                        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0IiwiaWF0IjoxNjY3Mzg2NTI4LCJleHAiOjE2Njc5OTEzMjh9.81eoWYT1g1ov6XsQBxBOf9COh6EGEKo6ziv5YxK4-kNMYE79MuTnSOWoFN0XejVTG3b9Ml5isnWf3fEhbXCwIw";
                        String post = RestTemplateUtil.builder().post(url, fbtoken, token, request);
                        log.debug("发送请求：" + post);
                        if (StringUtils.isBlank(post)) {
                            int code = (int) JSONObject.parseObject(post).get("code");
                            if (code == 200) {
                                setMap.putAll(map);
                                log.info("问卷时间结束停止数"+setMap.size());
                            }
                        }
                    }
                }
            }
        });
        long end = System.currentTimeMillis();
        log.info("查询结束："+(end-start));
    }
}
