package com.tduck.cloud.account.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tduck.cloud.account.entity.RoleEntity;
import com.tduck.cloud.account.mapper.RoleMapper;
import com.tduck.cloud.account.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.awt.image.RasterOp;
import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/9 17:23
 * <p>
 * mark
 */

@Slf4j
@Service("roleService")
@Component
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {

    @Override
    public List<RoleEntity> queryList(){
        List<RoleEntity> list = this.baseMapper.selectList(Wrappers.<RoleEntity>lambdaQuery());
        return list;
    }
}
