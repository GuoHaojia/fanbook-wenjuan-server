package com.tduck.cloud.api.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import com.tduck.cloud.project.entity.UserProjectLogicEntity;
import com.tduck.cloud.project.service.UserProjectLogicService;
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

    private final UserProjectLogicService projectLogicService;

    @Login
    @GetMapping("/role/list")
    @ApiOperation("用户角色列表")
    public Result queryList() {
        List<RoleEntity> list = roleService.queryList();
        return Result.success(list);
    }

    @Login
    @PostMapping("/role/update")
    @ApiOperation("用户角色修改和添加")
    public Result updateRole(@RequestBody RoleEntity roleEntity){
        if(roleEntity.getId() == null){
            return Result.success(roleService.save(roleEntity));
        }else{
            return Result.success(roleService.updateById(roleEntity));
        }
    }

    @Login
    @PostMapping("/role/group")
    @ApiOperation("查询角色下所有用户")
    public Result userListByRole(@RequestBody RoleVo roleVo){
        if(roleVo.getRoleid() == null){
            return Result.failed("角色不得为空");
        }

        return Result.success(userService.getUserByRole(roleVo));
    }

    @Login
    @PostMapping("/role/up")
    @ApiOperation("用户角色变更")
    public Result updateUserBelong(@RequestBody UserRoleVo userRoleVo){
        if(userRoleVo.getUserid() == null || userRoleVo.getRoleid() == null){
            return Result.failed("角色 用户不得为空");
        }
        userService.updateUserBelong(userRoleVo);
        return Result.success(userRoleVo.getId());
    }

    @Login
    @PostMapping("/role/view")
    @ApiOperation("用户角色逻辑")
    public Result queryRoleLogic(@RequestParam(name = "projectKey") String projectKey){
        List<UserProjectLogicEntity> entityList = projectLogicService.list(Wrappers.<UserProjectLogicEntity>lambdaQuery().eq(UserProjectLogicEntity::getProjectKey, projectKey).eq(UserProjectLogicEntity::getType,3));
        return Result.success(entityList);
    }
}
