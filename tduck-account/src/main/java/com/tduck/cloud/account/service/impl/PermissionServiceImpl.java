package com.tduck.cloud.account.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tduck.cloud.account.entity.AdminEntity;
import com.tduck.cloud.account.entity.PermissionEntity;
import com.tduck.cloud.account.entity.PermissionRoleEntity;
import com.tduck.cloud.account.entity.UserInfo;
import com.tduck.cloud.account.mapper.PermissionMapper;
import com.tduck.cloud.account.service.PermissionService;
import com.tduck.cloud.account.vo.PermissionRoleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 17:15
 * <p>
 * mark
 */

@Slf4j
@Service("permissionService")
@Component
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, PermissionEntity> implements PermissionService {
    @Override
    public List<PermissionEntity> selectList(){
        return this.baseMapper.selectList(Wrappers.<PermissionEntity>lambdaQuery());
    }

    @Override
    public List<PermissionEntity> selectListByRole(PermissionRoleVo permissionRoleVo) {
        return this.baseMapper.selectListByRole(permissionRoleVo);
    }

    @Override
    public List<PermissionEntity> selectListByUser(UserInfo userInfo){
        return this.baseMapper.selectAdminByUserId(userInfo);
    }

    @Override
    public Boolean roleUpPermissions(PermissionRoleVo permissionRoleVo){

        this.baseMapper.deleteRolePermission(permissionRoleVo);
        return this.baseMapper.insertRolePermission(permissionRoleVo);

        //this.baseMapper.delete(Wrappers.<PermissionEntity>lambdaQuery().eq(PermissionEntity::get,permissionRoleVo.getRoleid()));
    }
}
