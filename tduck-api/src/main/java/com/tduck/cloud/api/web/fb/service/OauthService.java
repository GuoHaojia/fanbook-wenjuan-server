package com.tduck.cloud.api.web.fb.service;

import cn.hutool.crypto.digest.DigestUtil;
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
import com.tduck.cloud.account.vo.Chat;
import com.tduck.cloud.account.vo.Sign;
import com.tduck.cloud.account.vo.UserPoint;
import com.tduck.cloud.common.constant.FanbookCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
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


    /**
     * 积分接口系列参数
     */
    @Value("${fb.open.api.scorehost}")
    String scorehost;

    @Value("${fb.open.api.appkey}")
    String appkey;

    @Value("${fb.open.api.appsecret}")
    String appsecret;

    @Value("${fb.open.api.getuserscore}")
    String getuserscore;

    @Value("${fb.open.api.modifyuserpoint}")
    String modifyuserpoint;


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
        log.debug("code:" + code);
        log.debug("parameter:" + openUrl + apiTokenUrl);
        log.debug("parameter:" + str11);
        log.debug("parameter:" + redirectUri);
        log.debug("rJson:" + rJson);
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

    public JSONArray getGuildMembers(String access_token, String guildId,Integer after,Integer limit){
        String rJson = OkHttpUtils
                .builder().url(robothost + "/" + access_token + "/getGuildMembers")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + access_token)
                .addParam("guild_id", guildId)
                .get()
                .sync();

        if(null != rJson){
            return JSONObject.parseObject(rJson).getJSONArray("result");
        }else{
            return null;
        }
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
            List<FanbookRole> retn = JSONObject.parseArray(js, FanbookRole.class);
            return retn;
        } else {
            return null;
        }

    }

    public List<FanbookMember> getRoleMembers(String access_token, String guildId, String roleId) {
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
            List<FanbookMember> retn = JSONObject.parseArray(js, FanbookMember.class, new ParserConfig(true));
            return retn;
        } else {
            return null;
        }
    }

    public Boolean setMemberRoles(String access_token,Long guildId, Long user_id,Long roles){
        List roleList = new ArrayList();
        roleList.add(roles);
        JSONObject data = new JSONObject();
        data.put("guild_id",guildId);
        data.put("user_id",user_id);
        data.put("roles",roleList);

        String rJson = OkHttpUtils
                .builder().url(robothost + "/" + access_token + "/setMemberRoles")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + access_token)
                .post(data.toJSONString())
                .sync();

        Logger.getLogger("角色权限").info("角色分配json返回"+rJson);
        if (null != rJson) {


            Boolean userJson = (Boolean)JSONObject.parseObject(rJson).get("result");
            return userJson;
        } else {
            return false;
        }
    }

    public String existsMember(String access_token , String guild_id, String member_id){
        String rJson = OkHttpUtils
                .builder().url(robothost + "/" + access_token + "/guild/existsMember")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + access_token)
                .addParam("guild_id",guild_id)
                .addParam("member_id",member_id)
                .post(true)
                .sync();

        return JSONObject.parseObject(rJson).getJSONObject("result").get("exists").toString();
    }

    public Chat getPrivateChat(String access_token , Long user_id){
        JSONObject data = new JSONObject();
        data.put("user_id",user_id);

        String rJson = OkHttpUtils
                .builder().url(robothost + "/" + access_token + "/getPrivateChat")
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + access_token)
                .post(data.toJSONString())
                .sync();

        Logger.getLogger("getPrivateChat").info(rJson);
        Chat chat = null;
        JSONObject chatJson = JSONObject.parseObject(rJson).getJSONObject("result");
        if (null != chatJson) {
            chat = chatJson.toJavaObject(Chat.class);
        }

        return chat;
    }

    public void modifyUserPoint(String bizId,Long guildId,Long fbLongId,Integer point,String remark){
        JSONObject data = new JSONObject();
        data.put("bizId",bizId);
        data.put("fbLongId",fbLongId);
        data.put("guildId",guildId);
        data.put("point",point);
        data.put("remark",remark);

        Sign sign = Sign.builder()
                .appKey(appkey)
                .nonce(UUID.randomUUID().toString())
                .timestamp(System.currentTimeMillis() + "")
                .build();

        Map<String, String> signmap = new LinkedHashMap<>();
        signmap.put("AppKey", sign.getAppKey());
        signmap.put("Nonce", sign.getNonce());
        signmap.put("Timestamp", sign.getTimestamp());
        signmap.put("requestBody", data.toJSONString());

        String rJson = OkHttpUtils
                .builder().url(scorehost + modifyuserpoint)
                .addHeader("AppKey", sign.getAppKey())
                .addHeader("Nonce", sign.getNonce())
                .addHeader("Timestamp", sign.getTimestamp())
                .addHeader("Signature", getSign(signmap))
                .addHeader("content-type", "application/json")
                .post(data.toJSONString())
                .sync();


        Logger.getLogger("积分接口").info(rJson);
    }

    public UserPoint getUserScore(String guildId, String userId) {

        Sign sign = Sign.builder()
                .appKey(appkey)
                .nonce(UUID.randomUUID().toString())
                .timestamp(System.currentTimeMillis() + "")
                .build();

        Map<String, String> signmap = new LinkedHashMap<>();
        signmap.put("AppKey", sign.getAppKey());
        signmap.put("Nonce", sign.getNonce());
        signmap.put("Timestamp", sign.getTimestamp());
        signmap.put("guildId", guildId);
        signmap.put("userId", userId);

        String rJson = OkHttpUtils
                .builder().url(scorehost + getuserscore)
                .addHeader("AppKey", sign.getAppKey())
                .addHeader("Nonce", sign.getNonce())
                .addHeader("Timestamp", sign.getTimestamp())
                .addHeader("Signature", getSign(signmap))
                .addHeader("content-type", "application/json")
                .addParam("guildId",guildId)
                .addParam("userId",userId)
                .get()
                .sync();

        UserPoint userPoint = null;
        JSONObject userJson = JSONObject.parseObject(rJson).getJSONObject("data");
        if (null != userJson) {
            userPoint = userJson.toJavaObject(UserPoint.class);
        }

        return userPoint;
    }

    private String getSign(Map<String, String> signmap) {
        try {
            //log.info(appsecret + "&" + generateParams(signmap, "") + "&" + appsecret);
            //测试
            return signEncode(appsecret + "&" + generateParams(signmap, "") + "&" + appsecret);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String signEncode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(DigestUtil.md5Hex(str,"UTF-8").toString(),"UTF-8");
    }

    private String generateParams(Map<String, String> params, String charset) throws UnsupportedEncodingException {

        int flag = 0;

        StringBuffer ret = new StringBuffer();

        Iterator iter = params.entrySet().iterator();

        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (val != null) {
                if (flag == 0) {
                    ret.append(key);
                    ret.append("=");
                    if (charset != null && !charset.equals("")) {
                        ret.append(URLEncoder.encode(val.toString(), charset));
                    } else {
                        ret.append(val.toString());
                    }
                    flag++;
                } else {
                    ret.append("&");
                    ret.append(key);
                    ret.append("=");
                    if (charset != null && !charset.equals("")) {
                        ret.append(URLEncoder.encode(val.toString(), charset));
                    } else {
                        ret.append(val.toString());

                    }
                }

            }

        }

        return ret.toString();
    }
}
