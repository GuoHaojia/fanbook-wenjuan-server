package com.tduck.cloud.api.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tduck.cloud.account.entity.*;
import com.tduck.cloud.account.service.AdminRoleService;
import com.tduck.cloud.account.service.AdminService;
import com.tduck.cloud.account.service.PermissionService;
import com.tduck.cloud.account.service.UserService;
import com.tduck.cloud.account.vo.*;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.api.web.fb.service.OauthService;
import com.tduck.cloud.common.util.Result;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.ArrayList;
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


    private final OauthService oauthService;
    private final UserService userService;

    /**
     *
     * 分类对管理一对多
     *
     * */
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


    @PostMapping("/fanbook/pullroles")
    @ApiOperation("批量拉取fanbook分类")
    public Result pullRoleFromFanbook(@RequestBody FanbookRoleVo fanbookRoleVo){
        return Result.success(oauthService.getGuildRoles(fanbookRoleVo.getToken(),fanbookRoleVo.getGuildId()));
    }

    @PostMapping("/fanbook/pullmembers")
    @ApiOperation("批量拉取fanbook成员")
    public Result pullMemberFromFanbook(@RequestBody FanbookRoleVo fanbookRoleVo){

        return Result.success(oauthService.getRoleMembers(fanbookRoleVo.getToken(),fanbookRoleVo.getGuildId(),fanbookRoleVo.getRoleId()));

    }

    @PostMapping("/fanbook/adminup")
    public Result updateAdminByFb(@RequestBody FanbookUpVo fanbookUpVo){
        if(fanbookUpVo.getRoleid() == null){
            //设置为表单管理
            fanbookUpVo.setRoleid(3L);
        }

        if(fanbookUpVo.getMembers().size() == 0){
            return Result.failed("用户记录不能为空");
        }else{

            Long userid;
            UserEntity userEntity;

            for(MemberInfo memberInfo : fanbookUpVo.getMembers()){
                //导入用户记录
                userEntity = UserEntity.builder()
                        .avatar(memberInfo.getAvatar())
                        .name(memberInfo.getFirst_name())
                        .fbUser(memberInfo.getId())
                        .fbUsername(memberInfo.getUsername())
                        .build();

                QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("fb_user", memberInfo.getId());

                UserEntity dbUserEntity = userService.getOne(queryWrapper);
                if (null != dbUserEntity) {
                    UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("fb_user", userEntity.getFbUser());
                    updateWrapper.set("avatar", userEntity.getAvatar());
                    updateWrapper.set("name", userEntity.getName());
                    userService.update(updateWrapper);
                    userid = dbUserEntity.getId();
                } else {
                    userService.save(userEntity);
                    userid = userEntity.getId();
                }

                //赋予权限
                adminService.updateUserBelong(AdminRoleVo.builder().userid(userid).roleid(fanbookUpVo.getRoleid()).rolestatus(1).build());
            }

        }

        return Result.success("添加成功");
    }
}
