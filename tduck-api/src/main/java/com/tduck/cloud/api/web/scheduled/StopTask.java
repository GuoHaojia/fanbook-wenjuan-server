package com.tduck.cloud.api.web.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.tduck.cloud.project.entity.ScheduledTaskEntity;
import com.tduck.cloud.project.service.ScheduledTaskService;
import com.tduck.cloud.project.util.RestTemplateUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

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
        String fbtoken = task.getParam().get("fbtoken").toString();
        String token = task.getParam().get("token").toString();
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
