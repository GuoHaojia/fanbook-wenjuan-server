package com.tduck.cloud.api.web.scheduled;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tduck.cloud.common.validator.ValidatorUtils;
import com.tduck.cloud.project.entity.ScheduledTaskEntity;
import com.tduck.cloud.project.service.FanbookService;
import com.tduck.cloud.project.service.ScheduledTaskService;
import com.tduck.cloud.project.service.UserPublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 定时任务持久到数据库，服务重启，未执行任务重载
 **/

@Component
@Slf4j
public class ScheduledService implements InitializingBean {

    @Autowired
    private ScheduledTaskService taskService;

    @Autowired
    private FanbookService fanbookService;

    @Autowired
    private UserPublishService userPublishService;

    @Value("${platform.front.baseUrl}")
    private String host;

    @Value("${server.servlet.context-path}")
    private String path;

    private static ScheduledThreadPoolExecutor scheduledExecutor;

    private static Map<Long, ScheduledFuture> futureMap = new ConcurrentHashMap<>();


    public boolean addTask(ScheduledTaskEntity task) throws ParseException {
        ValidatorUtils.validateEntity(task);
        if (futureMap.get(task.getId()) != null) {
            return false;
        }

        long l = LocalDateTime.now().withNano(0).until(task.getTime(), ChronoUnit.SECONDS);
        long delay = l > 0l ? l : 0l;
        ScheduledFuture<?> future = null;

        if (task.getType() == 1) {
            //        String url = host + path + "/user/project/stop";
            String url = "http://localhost:8999/mofang-api/user/project/stop";
            future = scheduledExecutor.schedule(new StopTask(task, url, taskService), delay, TimeUnit.SECONDS);
        }
        if (task.getType() == 2) {
            future = scheduledExecutor.schedule(new PublishTask(fanbookService, userPublishService, task, taskService), delay, TimeUnit.SECONDS);
        }

        futureMap.put(task.getId(), future);
        System.out.println(futureMap);

        return true;
    }

    public boolean updateTask(ScheduledTaskEntity task) throws ParseException {
        ValidatorUtils.validateEntity(task);
        if (futureMap.get(task.getId()) == null) {
            log.info("队列中无此任务");
            return false;
        }

        removeTask(task.getId());
        task.setFlag(0);
        addTask(task);

        return true;
    }

    public boolean removeTask(Long taskId){
        if (futureMap.get(taskId) == null) {
            log.info("队列中无此任务");
            return false;
        }

        ScheduledFuture future = futureMap.get(taskId);
        future.cancel(false);
        futureMap.remove(taskId);

        //任务已取消
        ScheduledTaskEntity task = taskService.getById(taskId).setFlag(1);
        taskService.updateById(task);
        log.info("任务已取消");

        return true;
    }

    //关闭定时任务服务
    public void shutdown(){
        scheduledExecutor.shutdown();
    }

    //初始化定时任务服务
    public void init() throws Exception {
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNamePrefix("schedule-pool-%d").setDaemon(true).build();
        if (scheduledExecutor !=null) {
            scheduledExecutor.shutdown();
        }
        System.out.println("111");
        scheduledExecutor = new ScheduledThreadPoolExecutor(3, threadFactory, new ThreadPoolExecutor.AbortPolicy());
        System.out.println("222");

//        if (!futureMap.isEmpty()) {
//            futureMap.clear();
//        }

        //查询数据库获取定时任务信息，载入到定时任务服务中
        List<ScheduledTaskEntity> list = taskService.list(Wrappers.<ScheduledTaskEntity>lambdaQuery().eq(ScheduledTaskEntity::getFlag, 0).ne(ScheduledTaskEntity::getStatus, 1));
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(taskEntity -> {
                try {
                    this.addTask(taskEntity);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }

    }
}
