package com.tduck.cloud.api.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tduck.cloud.account.entity.AdminEntity;
import com.tduck.cloud.account.entity.PermissionEntity;
import com.tduck.cloud.account.entity.UserInfo;
import com.tduck.cloud.account.service.AdminService;
import com.tduck.cloud.account.service.PermissionService;
import com.tduck.cloud.account.util.JwtUtils;
import com.tduck.cloud.account.vo.PermissionRoleVo;
import com.tduck.cloud.api.annotation.Login;
import com.tduck.cloud.api.exception.AuthorizationException;
import com.tduck.cloud.api.exception.RoleException;
import com.tduck.cloud.api.web.fb.service.OauthService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.sql.Wrapper;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qing
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    public static final String USER_KEY = "userId";
    private final JwtUtils jwtUtils;

    @Autowired
    OauthService oauthService;

    @Autowired
    PermissionService permissionService;

    @Value("${devdebug}")
    private boolean debug;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public AuthorizationInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
        } else {
            return true;
        }

        if (annotation == null) {
            return true;
        }


        if(debug){

            return true;

        }

        //????????????FB??????
        String fbtoken = request.getHeader("fbtoken");
        String token = request.getHeader("token");
        UserInfo user = oauthService.getMe(fbtoken);

        if(null==user)
        {
            throw new AuthorizationException("Fbtoken????????????????????????");
        }
        if (StrUtil.isBlank(token)) {
            throw new AuthorizationException("token????????????");
        }


        ///????????????????????????
        List<PermissionEntity> permissionEntityList =  permissionService.selectListByUser(user);


        String requestUri = request.getRequestURI().replace(contextPath,"");
        Boolean checkPermission = true;
        for(PermissionEntity permissionEntity : permissionEntityList){
            if(requestUri.indexOf(permissionEntity.getAction()) == 0){
                checkPermission = false;
                break;
            }
        }

        if(checkPermission){
            throw new RoleException("??????????????????");
        }
        ///????????????????????????

        //??????????????????
//        String token = request.getHeader(jwtUtils.getHeader());
//        if (StrUtil.isBlank(token)) {
//            token = request.getParameter(jwtUtils.getHeader());
//        }

        //????????????
        if (StrUtil.isBlank(token)) {
            throw new AuthorizationException(jwtUtils.getHeader() + "????????????");
        }

        Claims claims = jwtUtils.getClaimByToken(token);
//        if (claims == null || jwtUtils.isTokenExpired(claims.getExpiration())) {
//            throw new AuthorizationException(jwtUtils.getHeader() + "????????????????????????");
//        }

        //??????userId???request??????????????????userId?????????????????????
        request.setAttribute(USER_KEY, Long.parseLong(claims.getSubject()));

        return true;
    }
}
