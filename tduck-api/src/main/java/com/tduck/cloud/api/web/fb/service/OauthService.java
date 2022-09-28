package com.tduck.cloud.api.web.fb.service;

import com.alibaba.fastjson.JSONObject;
import com.tduck.cloud.account.entity.UserInfo;
import com.tduck.cloud.account.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

}
