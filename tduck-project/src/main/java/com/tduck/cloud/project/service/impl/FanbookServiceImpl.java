package com.tduck.cloud.project.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tduck.cloud.project.service.FanbookService;
import com.tduck.cloud.project.util.RestTemplateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("fanbookService")
public class FanbookServiceImpl implements FanbookService {
    @Value("${fb.open.api.sendMessage}")
    String sendMessageUrl;
    @Value("${fb.open.api.robothost}")
    String robothost;
    @Value("${fb.bot.token}")
    String botToke;

    @Override
    public String sendMessage(JSONObject jsonObject) {
        String url=robothost + "/bot" + botToke + sendMessageUrl;
        System.out.println(url);
        System.out.println(jsonObject);
        return RestTemplateUtil.builder().post(url, jsonObject);
    }
}
