package com.tduck.cloud.account.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tduck.cloud.account.entity.PermissionEntity;
import com.tduck.cloud.account.entity.RoleEntity;
import com.tduck.cloud.account.entity.UserInfo;
import com.tduck.cloud.account.vo.PermissionRoleVo;
import com.tduck.cloud.account.vo.UserRoleVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 17:14
 * <p>
 * mark
 */

@Service
public interface PermissionService {
    List<PermissionEntity> selectList();
    List<PermissionEntity> selectListByRole(PermissionRoleVo permissionRoleVo);
    List<PermissionEntity> selectListByUser(UserInfo userInfo);
    Boolean roleUpPermissions(PermissionRoleVo permissionRoleVo);
}
