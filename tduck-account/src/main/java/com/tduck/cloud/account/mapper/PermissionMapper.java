package com.tduck.cloud.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tduck.cloud.account.entity.AdminEntity;
import com.tduck.cloud.account.entity.PermissionEntity;
import com.tduck.cloud.account.entity.UserInfo;
import com.tduck.cloud.account.vo.PermissionRoleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 17:15
 * <p>
 * mark
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {
    List<PermissionEntity> selectAdminByUserId(UserInfo userInfo);
    List<PermissionEntity> selectListByRole(PermissionRoleVo permissionRoleVo);
    Boolean deleteRolePermission(PermissionRoleVo permissionRoleVo);
    Boolean insertRolePermission(PermissionRoleVo permissionRoleVo);
}
