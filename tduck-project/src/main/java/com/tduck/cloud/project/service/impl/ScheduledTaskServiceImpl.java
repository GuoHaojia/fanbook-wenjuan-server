package com.tduck.cloud.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tduck.cloud.project.entity.ScheduledTaskEntity;
import com.tduck.cloud.project.mapper.ScheduledTaskMapper;
import com.tduck.cloud.project.service.ScheduledTaskService;
import org.springframework.stereotype.Service;

@Service("scheduledTaskService")
public class ScheduledTaskServiceImpl extends ServiceImpl<ScheduledTaskMapper, ScheduledTaskEntity> implements ScheduledTaskService {
}
