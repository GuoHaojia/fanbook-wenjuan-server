package com.tduck.cloud.api.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tduck.cloud.account.entity.FbGuild;
import com.tduck.cloud.account.entity.UserEntity;
import com.tduck.cloud.account.service.UserService;
import com.tduck.cloud.account.entity.UserInfo;
import com.tduck.cloud.account.util.JwtUtils;
import com.tduck.cloud.account.vo.UserPoint;
import com.tduck.cloud.api.util.HttpUtils;
import com.tduck.cloud.api.web.fb.service.OauthService;
import com.tduck.cloud.common.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@RequestMapping("/fanbook")
@RestController
@RequiredArgsConstructor
public class UserLoginFBController {

    @Value("${fb.open.fanbook_oauth_page}")
    String fanbookOauthPage;
    private final OauthService oauthService;
    private final UserService userService;

    @GetMapping("/getuserscore")
    public Result getUserScore(@RequestParam("guild_id") String guild_id,@RequestParam("member_id") String member_id){
        UserPoint userPoint = oauthService.getUserScore(guild_id,member_id);
        return Result.success(userPoint);
    }

    /**
     * 查询当前服务器是否存在积分商城
     * */
    @RequestMapping(value = "/exist/score", method = RequestMethod.GET)
    public Result existScoreShop(@RequestParam("guild_id") String guild_id,@RequestParam("member_id") String member_id,@RequestParam("access_token") String access_token) {

        return Result.success(oauthService.existsMember(access_token,guild_id,member_id));
    }

    /**
     * 授权回调地址
     *
     * @return Msg
     */
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Result login(HttpServletRequest request, @RequestParam("code") String code) {
        JSONObject tokenJson = oauthService.getToken(code);
        String access_token = tokenJson.getString("access_token");
        String ip = HttpUtils.getIpAddr(request);
        System.out.println("IP:" + ip);
        if (null != access_token) {
            UserInfo userInfo = oauthService.getMe(access_token);
            if (null != userInfo) {
                UserEntity userEntity = UserEntity.builder()
                        .avatar(userInfo.getAvatar())
                        .name(userInfo.getNickname())
                        .fbUser(userInfo.getUser_id().toString())
                        .fbUsername(userInfo.getUsername().toString())
                        .lastLoginIp(ip)
                        .build();
                QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("fb_user", userInfo.getUser_id().toString());
                UserEntity dbUserEntity = userService.getOne(queryWrapper);
                if (null != dbUserEntity) {
                    UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("fb_user", userInfo.getUser_id().toString());
                    updateWrapper.set("avatar", userInfo.getAvatar());
                    updateWrapper.set("name", userInfo.getNickname());
                    userService.update(updateWrapper);
                } else {
                    userService.save(userEntity);
                }
            }
            return userService.accountFBLogin(userInfo.getUser_id().toString(), ip, access_token);
        }
        return Result.failed();
    }


    /**
     * 重定向
     */
    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
    public void redirect(HttpServletResponse response) {
        try {
            response.sendRedirect(fanbookOauthPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前登录者信息
     */
    @ResponseBody
    @RequestMapping(value = "/getMe", method = RequestMethod.GET)
    public Result getMe(@RequestHeader("fbtoken") @Validated String token) {
//        Object tokenObj = request.getHeader("fbtoken");
        UserInfo user = oauthService.getMe(token);
        return Result.success(user);

    }

    /**
     * 获取服务器列表
     */
    @ResponseBody
    @RequestMapping(value = "/getGuilds", method = RequestMethod.GET)
    public Result getGuilds(@RequestHeader("fbtoken") @Validated String token) {
//        Object tokenObj = request.getHeader("fbtoken");
        JSONObject guilJson = oauthService.getGuilds(token);
        UserInfo user = oauthService.getMe(token);
        if (null == guilJson) {
            return Result.failed("服务器数据获取失败，请检查token是否有效！");
        }
        if (null == user) {
            return Result.failed("服务器数据获取失败，可能是token过期，请重新登录！");
        }
        List<FbGuild> list = guilJson.getJSONArray("data").toJavaList(FbGuild.class);
        List<FbGuild> newList = new ArrayList<>();
        list.forEach(fbGuild -> {
//            if (fbGuild.getOwner().equals(user.getUser_id().toString())) {
                List<JSONObject> channels = fbGuild.getChannels();
                List<JSONObject> channelJson = new ArrayList<>();

                channels.forEach(obj -> {
                    JSONObject channel = JSONObject.parseObject(obj.toString());
                    channel.put("guild_name", fbGuild.getName());
                    channelJson.add(channel);
                });
                fbGuild.setChannels(channelJson);
                newList.add(fbGuild);
//            }
        });
//        Iterator<FbGuild> iter = list.iterator();
//        while (iter.hasNext()) {
//            FbGuild fbg = iter.next();
//            if (!fbg.getOwner().equals(user.getUser_id().toString())) {
//                iter.remove();
//            }
//        }

        return Result.success(newList);

    }
}
