package com.tduck.cloud.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tduck.cloud.project.entity.PublishEntity;
import com.tduck.cloud.project.mapper.UserPublishMapper;
import com.tduck.cloud.project.service.UserPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("userPublishService")
@RequiredArgsConstructor
public class UserPublishServiceImpl extends ServiceImpl<UserPublishMapper, PublishEntity> implements UserPublishService {
}
