package com.tduck.cloud.api.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tduck.cloud.account.entity.RoleEntity;
import com.tduck.cloud.account.entity.UserAuthorizeEntity;
import com.tduck.cloud.account.entity.UserEntity;
import com.tduck.cloud.account.entity.enums.UserAuthorizeTypeEnum;
import com.tduck.cloud.account.service.RoleService;
import com.tduck.cloud.account.service.UserAuthorizeService;
import com.tduck.cloud.account.service.UserService;
import com.tduck.cloud.account.service.UserValidateService;
import com.tduck.cloud.account.vo.RoleVo;
import com.tduck.cloud.account.vo.UserDetailVO;
import com.tduck.cloud.account.vo.UserRoleVo;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.wx.mp.entity.WxMpUserEntity;
import com.tduck.cloud.wx.mp.service.WxMpUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;


/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/9 16:07
 * <p>
 * mark
 */
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
@Api
public class RoleController {
    private final UserService userService;
    private final RoleService roleService;
    private final UserAuthorizeService userAuthorizeService;
    private final UserValidateService userValidateService;

    @Login
    @GetMapping("/role/list")
    @ApiOperation("用户角色列表")
    public Result queryList() {
        List<RoleEntity> list = roleService.queryList();
        return Result.success(list);
    }

    @Login
    @PostMapping("/role/update")
    @ApiOperation("用户角色修改")
    public Result updateRole(@RequestBody RoleEntity roleEntity){
        return Result.success(roleService.updateById(roleEntity));
    }

    @Login
    @PostMapping("/role/group")
    @ApiOperation("查询角色下所有用户")
    public Result userListByRole(@RequestBody RoleVo roleVo){
        return Result.success(userService.getUserByRole(roleVo));
    }

    @Login
    @PostMapping("/role/up")
    @ApiOperation("用户角色变更")
    public Result updateUserBelong(@RequestBody UserRoleVo userRoleVo){
        userService.updateUserBelong(userRoleVo);
        return Result.success(userRoleVo.getId());
    }
}
