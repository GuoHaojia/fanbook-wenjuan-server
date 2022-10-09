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
import com.tduck.cloud.account.vo.UserDetailVO;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.wx.mp.entity.WxMpUserEntity;
import com.tduck.cloud.wx.mp.service.WxMpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
public class RoleController {
    private final UserService userService;
    private final RoleService roleService;
    private final UserAuthorizeService userAuthorizeService;
    private final UserValidateService userValidateService;

    @Login
    @GetMapping("/role/list")
    public Result queryList() {
        List<RoleEntity> list = roleService.queryList();
        return Result.success(list);
    }
}
