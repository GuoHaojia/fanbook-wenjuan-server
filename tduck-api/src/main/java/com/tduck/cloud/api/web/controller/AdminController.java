package com.tduck.cloud.api.web.controller;

import com.tduck.cloud.account.entity.AdminEntity;
import com.tduck.cloud.account.service.AdminService;
import com.tduck.cloud.account.service.PermissionService;
import com.tduck.cloud.account.vo.AdminRoleVo;
import com.tduck.cloud.account.vo.AdminVo;
import com.tduck.cloud.account.vo.PermissionRoleVo;
import com.tduck.cloud.account.vo.RoleVo;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.common.util.Result;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 15:01
 * <p>
 * mark
 */

@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final PermissionService permissionService;

    /**
     *
     * 分类对管理一对多
     *
     * */
    @Login
    @PostMapping("/role/group")
    @ApiOperation("后端管理员列表")
    public Result queryList(@RequestBody AdminVo adminVo){
        return Result.success(adminService.adminListByRole(adminVo));
    }


    @Login
    @PostMapping("/role/update")
    @ApiOperation("管理员角色修改")
    public Result updateRole(@RequestBody AdminEntity adminEntity){
        if(adminEntity.getId() == null){
            return Result.success(adminService.save(adminEntity));
        }else {
            return Result.success(adminService.updateById(adminEntity));
        }
    }

    @Login
    @GetMapping("/role/list")
    @ApiOperation("管理员角色列表")
    public Result queryList() {
        return Result.success(adminService.queryList());
    }

    @Login
    @PostMapping("/role/up")
    @ApiOperation("管理员角色变更")
    public Result updateUserBelong(@RequestBody AdminRoleVo adminRoleVo){
        if(adminRoleVo.getRoleid() == null || adminRoleVo.getUserid() == null){
            return Result.failed("管理员 用户不得为空");
        }
        adminService.updateUserBelong(adminRoleVo);
        return Result.success(adminRoleVo.getId());
    }

    @Login
    @GetMapping("/permission/list")
    @ApiOperation("获取管理员可选权限列表")
    public Result permissionList(){
        return Result.success(permissionService.selectList());
    }

    @Login
    @PostMapping("/permission/up")
    @ApiOperation("修改管理员角色具备得权限")
    public Result roleUpPermissions(@RequestBody PermissionRoleVo permissionRoleVo){

        if (permissionRoleVo.getRoleid() == null){
            return Result.failed("roleid不得为空");
        }

        Logger log = Logger.getLogger("测试");
        log.info(permissionRoleVo.toString());
        return Result.success(permissionService.roleUpPermissions(permissionRoleVo));
    }


    @Login
    @PostMapping("/role/permissions")
    @ApiOperation("获取管理员角色所有权限")
    public Result rolePermissionList(@RequestBody PermissionRoleVo permissionRoleVo){

        if (permissionRoleVo.getRoleid() == null){
            return Result.failed("roleid不得为空");
        }

        return Result.success(permissionService.selectListByRole(permissionRoleVo));
    }

    /**
     *  获取成员并且分类好  后续批量导入系统
     */

    @Login
    @PostMapping("/fanbook/pullmembers")
    @ApiOperation("批量拉取fanbook成员")
    public Result pullMemberFromFanbook(){
        return Result.success();
    }
}
