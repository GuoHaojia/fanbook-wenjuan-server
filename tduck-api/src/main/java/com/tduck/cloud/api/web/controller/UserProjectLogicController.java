package com.tduck.cloud.api.web.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.common.util.Result;
import com.tduck.cloud.project.entity.UserProjectLogicEntity;
import com.tduck.cloud.project.service.UserProjectLogicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;

/**
 * @author : smalljop
 * @description : 项目逻辑
 * @create : 2020-11-18 18:17
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserProjectLogicController {

    private final UserProjectLogicService projectLogicService;
    @Value("${fb.open.fanbook_oauth_page}")
    String fanbookOauthPage;
    @Login
    @PostMapping("/user/project/logic/save")
    public Result saveUserProjectLogic(@RequestBody UserProjectLogicEntity userProjectLogicEntity) {
        projectLogicService.saveOrUpdate(userProjectLogicEntity);
        return Result.success(userProjectLogicEntity);
    }


    @Login
    @PostMapping("/user/project/logic/delete")
    public Result deleteUserProjectLogic(@RequestBody UserProjectLogicEntity userProjectLogicEntity) {
        return Result.success(projectLogicService.removeById(userProjectLogicEntity));
    }

    @GetMapping("/user/project/logic/list")
    public Result queryProjectItem(@RequestParam @NotBlank String projectKey) {

        List<UserProjectLogicEntity> entityList = projectLogicService.list(Wrappers.<UserProjectLogicEntity>lambdaQuery().eq(UserProjectLogicEntity::getProjectKey, projectKey).lt(UserProjectLogicEntity::getType,3));
        return Result.success(entityList);
    }
}