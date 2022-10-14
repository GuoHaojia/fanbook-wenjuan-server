package com.tduck.cloud.api.web.fb.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tduck.cloud.account.entity.BotInfo;
import com.tduck.cloud.account.entity.FanbookMember;
import com.tduck.cloud.account.entity.FanbookRole;
import com.tduck.cloud.account.entity.UserInfo;
import com.tduck.cloud.account.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Service
@Slf4j
public class OauthService {
    @Value("${fb.open.api.openhost}")
    String openUrl;
    @Value("${fb.open.api.token}")
    String apiTokenUrl;
    @Value("${fb.open.api.refresh_token}")
    String apiRefresTokenUrl;
    @Value("${fb.open.api.getMe}")
    String apiGetMeUrl;
    @Value("${fb.open.api.getGuilds}")
    String apiGetGuildsUrl;
    @Value("${fb.open.client}")
    String clientID;
    @Value("${fb.open.sceret}")
    String sceret;
    @Value("${fb.open.redirect_uri}")
    String redirectUri;

    @Value("${fb.open.api.robothost}")
    String robothost;

    public JSONObject getToken(String code) {
        String base64String = clientID + ":" + sceret;
//        byte[] result1 = Base64.encodeBase64(base64String.getBytes());
        String str11 = Base64.getMimeEncoder().encodeToString(base64String.getBytes(StandardCharsets.UTF_8));
//        String str11 = Base64.encode(base64String);
//        String str11 = new String(result1);
        String rJson = OkHttpUtils
                .builder().url(openUrl + apiTokenUrl)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("authorization", "Basic " + str11)
                .addParam("grant_type", "authorization_code")
                .addParam("code", code)
                .addParam("redirect_uri", redirectUri)
                .post(false)
                .sync();
        log.debug("getToken");
        log.debug("code:"+code);
        log.debug("parameter:" + openUrl + apiTokenUrl);
        log.debug("parameter:" + str11);
        log.debug("parameter:" + redirectUri);
        log.debug("rJson:"+rJson);
        return JSONObject.parseObject(rJson);
    }

    public UserInfo getMe(String access_token) {
        String rJson = OkHttpUtils
                .builder().url(openUrl + apiGetMeUrl)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + access_token)
                .get()
                .sync();

        UserInfo userInfo = null;
        if (null != rJson) {
            JSONObject userJson = JSONObject.parseObject(rJson).getJSONObject("data");
            if (null != userJson) {
                userInfo = userJson.toJavaObject(UserInfo.class);
            }
        }
        return userInfo;
    }
    public JSONObject getGuilds(String access_token) {
        String rJson = OkHttpUtils
                .builder().url(openUrl + apiGetGuildsUrl)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + access_token)
                .get()
                .sync();
        return JSONObject.parseObject(rJson);
    }


    public BotInfo getBotMe(String access_token) {
        String rJson = OkHttpUtils
                .builder().url(robothost + "/" + access_token + "/getMe")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + access_token)
                .get()
                .sync();

        Logger logger = Logger.getLogger("ceshi");
        logger.info(rJson);
        logger.info(robothost + access_token + "/getMe");

        BotInfo botInfo = null;
        if (null != rJson) {
            JSONObject userJson = JSONObject.parseObject(rJson).getJSONObject("result");
            if (null != userJson) {
                botInfo = userJson.toJavaObject(BotInfo.class);
            }
        }
        return botInfo;
    }


    public List<FanbookRole> getGuildRoles(String access_token, String guildId) {
        String rJson = OkHttpUtils
                .builder().url(robothost + "/" + access_token + "/getGuildRoles")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + access_token)
                .addParam("guild_id", guildId)
                .get()
                .sync();


        if (null != rJson) {
            JSONArray userJson = JSONObject.parseObject(rJson).getJSONArray("result");
            String js = JSONObject.toJSONString(userJson);
            List<FanbookRole> retn = JSONObject.parseArray(js,FanbookRole.class);
            return retn;
        }else{
            return null;
        }

    }

    public List<FanbookMember> getRoleMembers(String access_token, String guildId, String roleId){
        String rJson = OkHttpUtils
                .builder().url(robothost + "/" + access_token + "/getRoleMembers")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + access_token)
                .addParam("guild_id", guildId)
                .addParam("role_id", roleId)
                .get()
                .sync();

        if (null != rJson) {
            JSONArray userJson = JSONObject.parseObject(rJson).getJSONArray("result");

            String js = JSONObject.toJSONString(userJson);
            List<FanbookMember> retn = JSONObject.parseArray(js,FanbookMember.class,new ParserConfig(true));
            return retn;
        }else{
            return null;
        }
    }

}
