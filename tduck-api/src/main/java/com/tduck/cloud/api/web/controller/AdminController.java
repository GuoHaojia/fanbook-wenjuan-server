package com.tduck.cloud.api.web.controller;

import com.tduck.cloud.account.entity.AdminEntity;
import com.tduck.cloud.account.service.AdminService;
import com.tduck.cloud.account.vo.AdminRoleVo;
import com.tduck.cloud.account.vo.AdminVo;
import com.tduck.cloud.account.vo.RoleVo;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.common.util.Result;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.List;

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
    @ApiOperation("用户角色修改")
    public Result updateRole(@RequestBody AdminEntity adminEntity){
        return Result.success(adminService.updateById(adminEntity));
    }

    @Login
    @GetMapping("/role/list")
    @ApiOperation("管理员角色列表")
    public Result queryList() {
        return Result.success(adminService.queryList());
    }

    @Login
    @PostMapping("/role/up")
    @ApiOperation("用户角色变更")
    public Result updateUserBelong(@RequestBody AdminRoleVo adminRoleVo){
        adminService.updateUserBelong(adminRoleVo);
        return Result.success(adminRoleVo.getId());
    }
}
