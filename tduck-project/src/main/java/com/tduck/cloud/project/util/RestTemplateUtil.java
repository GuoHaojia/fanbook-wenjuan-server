package com.tduck.cloud.project.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class RestTemplateUtil {


    public static RestTemplateUtil builder() {
        return new RestTemplateUtil();
    }

    public String get(String url, JSONObject postData) {
        RestTemplate client = new RestTemplate();
        if (null != postData) {
            url=url.concat("?");
            for (Map.Entry entry : postData.entrySet()) {
                url=url.concat(entry.getKey().toString().concat("=").concat(entry.getValue().toString()).concat("&"));
            }
        }
        ResponseEntity<String> forEntity=client.getForEntity(url, String.class);
        return forEntity.getBody();
    }

    public String post(String url, JSONObject postData) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        //        JSONObject returnJson = client.postForEntity(url, postData, JSONObject.class,headers).getBody();
        HttpEntity<JSONObject> entity = new HttpEntity<>(postData, headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = client.postForEntity(url, entity, String.class);
            return responseEntity.getBody();
        } catch (RestClientException e) {

        }
        return null;
    }

    public String post(String url, String token, JSONObject postData) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("authorization", "Bearer " + token);
        HttpEntity<JSONObject> entity = new HttpEntity<>(postData, headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = client.postForEntity(url, entity, String.class);
            return responseEntity.getBody();
        } catch (RestClientException e) {

        }
        return null;
    }
}
