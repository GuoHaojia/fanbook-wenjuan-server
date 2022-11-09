package com.tduck.cloud.api.web.scheduled;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tduck.cloud.project.entity.PublishEntity;
import com.tduck.cloud.project.entity.ScheduledTaskEntity;
import com.tduck.cloud.project.service.FanbookService;
import com.tduck.cloud.project.service.ScheduledTaskService;
import com.tduck.cloud.project.service.UserPublishService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishTask implements Runnable {
    private FanbookService fanbookService;
    private UserPublishService userPublishService;
    private ScheduledTaskEntity task;
    private ScheduledTaskService taskService;

    @Override
    public void run() {



        String rstr = fanbookService.sendMessage((JSONObject) JSONObject.toJSON(task.getParam().get("jsonObject")));
        Boolean aBoolean = (Boolean) JSONObject.parseObject(rstr).get("ok");
        String entity1 = JSON.toJSONString(task.getParam().get("entity"));
        PublishEntity entity = JSON.parseObject(entity1, PublishEntity.class);
        entity.setStatus(aBoolean ? 1 : 2);
        userPublishService.updateById(entity);
        log.debug("发送文件返回：" + rstr);

        //任务已执行
        task.setStatus(aBoolean ? 1 : 2);
        taskService.updateById(task);

    }
}
