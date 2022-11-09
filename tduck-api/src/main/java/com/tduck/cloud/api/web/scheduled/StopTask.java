package com.tduck.cloud.api.web.scheduled;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tduck.cloud.project.entity.ScheduledTaskEntity;
import com.tduck.cloud.project.entity.UserProjectEntity;
import com.tduck.cloud.project.entity.UserProjectSettingEntity;
import com.tduck.cloud.project.entity.enums.ProjectStatusEnum;
import com.tduck.cloud.project.service.ScheduledTaskService;
import com.tduck.cloud.project.service.UserProjectService;
import com.tduck.cloud.project.service.UserProjectSettingService;
import com.tduck.cloud.project.util.RestTemplateUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopTask implements Runnable {
    private ScheduledTaskEntity task;
    private String url;
    private ScheduledTaskService taskService;

    @Override
    public void run() {

        JSONObject request = new JSONObject();
        request.put("key", task.getKey());
        String fbtoken = "mIOHt38z2kZCMlRqIOLY6g==";
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0IiwiaWF0IjoxNjY3Mzg2NTI4LCJleHAiOjE2Njc5OTEzMjh9.81eoWYT1g1ov6XsQBxBOf9COh6EGEKo6ziv5YxK4-kNMYE79MuTnSOWoFN0XejVTG3b9Ml5isnWf3fEhbXCwIw";
        String post = RestTemplateUtil.builder().post(url, fbtoken, token, request);
        log.debug("发送请求：" + post);
        int code = (int) JSONObject.parseObject(post).get("code");

        //任务已执行
        task.setStatus(code == 200 ? 1 : 2);
        taskService.updateById(task);

    }





//    @Scheduled(cron = "*/2 * * * * ?")
//    public void stopQuestionnaire () {
//        long start = System.currentTimeMillis();
//        log.info("查询问卷设置");
//
//        List<UserProjectSettingEntity> settings = userProjectSettingService.list(Wrappers.<UserProjectSettingEntity>lambdaQuery().le(UserProjectSettingEntity::getEndTime, LocalDateTime.now()));
//        Map<String, LocalDateTime> map = settings.stream().collect(Collectors.toMap(UserProjectSettingEntity::getProjectKey, UserProjectSettingEntity::getEndTime));
//        map.forEach((k, v) -> {
//            if (!setMap.containsKey(k)) {
//                UserProjectEntity upe = projectService.getOne(Wrappers.<UserProjectEntity>lambdaQuery().eq(UserProjectEntity::getKey, k));
//                if (ObjectUtil.isNotNull(upe) && upe.getStatus() != ProjectStatusEnum.STOP) {
//
//                }
//            }
//        });
//        long end = System.currentTimeMillis();
//        log.info("查询结束："+(end-start));
//    }
}
