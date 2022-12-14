package com.tduck.cloud.common.constant;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FanbookCard {
    static String wenJuanString = "{\"type\":\"column\",\"children\":[{\"type\":\"ic_title\",\"param\":{\"text\":\"TilteName\",\"icon\":\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fstatic.haoapp.mobi%2Ftrochili%2F88%2F8c%2F888cbad498d8686a2432b422cc1cbba868421bb7-356d7a56d100830c0b187bcd45260c8ad6dd41aa.png&refer=http%3A%2F%2Fstatic.haoapp.mobi&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1650093049&t=ac62ba764c5ebdec4a75fd39f3f1cb68\",\"bg\":0,\"line\":0,\"status\":0}},{\"type\":\"text\",\"param\":{\"text\":\"TextValue\",\"type\":0,\"line\":0}},{\"type\":\"button\",\"param\":{\"list\":[{\"type\":1,\"text\":\"填写问卷\",\"enable\":1,\"event\":{\"method\":\"mini_program\",\"param\":{\"appId\":\"BtUrl\"}}}]}}]}";
    static String PrizeString = "{\"type\":\"column\",\"children\":[{\"type\":\"ic_title\",\"param\":{\"text\":\"TilteName\",\"icon\":\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fstatic.haoapp.mobi%2Ftrochili%2F88%2F8c%2F888cbad498d8686a2432b422cc1cbba868421bb7-356d7a56d100830c0b187bcd45260c8ad6dd41aa.png&refer=http%3A%2F%2Fstatic.haoapp.mobi&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1650093049&t=ac62ba764c5ebdec4a75fd39f3f1cb68\",\"bg\":0,\"line\":0,\"status\":0}},{\"type\":\"text\",\"param\":{\"text\":\"TextValue\",\"type\":0,\"line\":0}}]}";

    static String NewPrizeString = "{\"chat_id\":$chatid,\"text\":\"{\\\"notification\\\":\\\"奖励通知\\\",\\\"data\\\":\\\"{  \\\\\\\"tag\\\\\\\": \\\\\\\"container\\\\\\\",  \\\\\\\"child\\\\\\\": {    \\\\\\\"tag\\\\\\\": \\\\\\\"column\\\\\\\",    \\\\\\\"children\\\\\\\": [      {        \\\\\\\"tag\\\\\\\": \\\\\\\"container\\\\\\\",        \\\\\\\"padding\\\\\\\": \\\\\\\"12,20,12,20\\\\\\\",        \\\\\\\"width\\\\\\\": 1024,        \\\\\\\"child\\\\\\\": {          \\\\\\\"tag\\\\\\\": \\\\\\\"text\\\\\\\",          \\\\\\\"data\\\\\\\": \\\\\\\"$CONTENT\\\\\\\",          \\\\\\\"style\\\\\\\": {            \\\\\\\"color\\\\\\\": \\\\\\\"#000000\\\\\\\",            \\\\\\\"fontSize\\\\\\\": 16,            \\\\\\\"fontWeight\\\\\\\": \\\\\\\"medium\\\\\\\"          }        }      }  ]  }}\\\",\\\"come_from_name\\\":\\\"问卷机器人服务器\\\",\\\"type\\\":\\\"messageCard\\\",\\\"come_from_icon\\\":\\\"https://fb-cdn.fanbook.mobi/fanbook/app/files/chatroom/circleIcon/2f8971916323bfc19cd843f6e7674e08\\\"}\",\"parse_mode\":\"Fanbook\"}";

    static String CDkString = "{\"chat_id\":$chatid,\"text\":\"{\\\"notification\\\":\\\"奖励通知\\\",\\\"data\\\":\\\"{  \\\\\\\"tag\\\\\\\": \\\\\\\"container\\\\\\\",  \\\\\\\"child\\\\\\\": {    \\\\\\\"tag\\\\\\\": \\\\\\\"column\\\\\\\",    \\\\\\\"children\\\\\\\": [      {        \\\\\\\"tag\\\\\\\": \\\\\\\"container\\\\\\\",        \\\\\\\"padding\\\\\\\": \\\\\\\"12,20,12,2\\\\\\\",        \\\\\\\"width\\\\\\\": 1024,        \\\\\\\"child\\\\\\\": {          \\\\\\\"tag\\\\\\\": \\\\\\\"text\\\\\\\",          \\\\\\\"data\\\\\\\": \\\\\\\"$CONTENT\\\\\\\",          \\\\\\\"style\\\\\\\": {            \\\\\\\"color\\\\\\\": \\\\\\\"#000000\\\\\\\",            \\\\\\\"fontSize\\\\\\\": 16,            \\\\\\\"fontWeight\\\\\\\": \\\\\\\"medium\\\\\\\"          }        }      },      {        \\\\\\\"tag\\\\\\\": \\\\\\\"container\\\\\\\",        \\\\\\\"padding\\\\\\\": \\\\\\\"12\\\\\\\",        \\\\\\\"width\\\\\\\": 1024,        \\\\\\\"child\\\\\\\": {          \\\\\\\"type\\\\\\\": \\\\\\\"outlined\\\\\\\",          \\\\\\\"tag\\\\\\\": \\\\\\\"button\\\\\\\",       \\\\\\\"type\\\\\\\": \\\\\\\"copy\\\\\\\",          \\\\\\\"href\\\\\\\": \\\\\\\"cdkCode\\\\\\\",          \\\\\\\"border\\\\\\\": \\\\\\\"1,69b1eb\\\\\\\",          \\\\\\\"child\\\\\\\" : {            \\\\\\\"tag\\\\\\\" : \\\\\\\"text\\\\\\\",            \\\\\\\"data\\\\\\\" : \\\\\\\"复制兑换码\\\\\\\"          }        }      }    ]  }}\\\",\\\"come_from_name\\\":\\\"问卷机器人服务器\\\",\\\"type\\\":\\\"messageCard\\\",\\\"come_from_icon\\\":\\\"https://fb-cdn.fanbook.mobi/fanbook/app/files/chatroom/circleIcon/2f8971916323bfc19cd843f6e7674e08\\\"}\",\"parse_mode\":\"Fanbook\"}";

    static String NewCDkString = "{\n" +
            "    \"parse_mode\": \"Fanbook\",\n" +
            "    \"text\": \"{\\\"data\\\":\\\"{\\\\\\\"tag\\\\\\\":\\\\\\\"column\\\\\\\",\\\\\\\"crossAxisAlignment\\\\\\\":\\\\\\\"stretch\\\\\\\",\\\\\\\"children\\\\\\\":[{\\\\\\\"tag\\\\\\\":\\\\\\\"row\\\\\\\",\\\\\\\"padding\\\\\\\":\\\\\\\"12,10,12,21\\\\\\\",\\\\\\\"children\\\\\\\":[{\\\\\\\"tag\\\\\\\":\\\\\\\"markdown\\\\\\\",\\\\\\\"flex\\\\\\\":\\\\\\\"tight\\\\\\\",\\\\\\\"data\\\\\\\":\\\\\\\"$CONTENT\\\\\\\"}]},{\\\\\\\"tag\\\\\\\":\\\\\\\"container\\\\\\\",\\\\\\\"child\\\\\\\":{\\\\\\\"tag\\\\\\\":\\\\\\\"column\\\\\\\",\\\\\\\"crossAxisAlignment\\\\\\\":\\\\\\\"start\\\\\\\",\\\\\\\"padding\\\\\\\":\\\\\\\"12,0,12,12\\\\\\\",\\\\\\\"children\\\\\\\":[{\\\\\\\"tag\\\\\\\":\\\\\\\"button\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"copy\\\\\\\",\\\\\\\"href\\\\\\\":\\\\\\\"cdkCode\\\\\\\",\\\\\\\"border\\\\\\\":\\\\\\\"1,#198CFE\\\\\\\",\\\\\\\"child\\\\\\\":{\\\\\\\"tag\\\\\\\":\\\\\\\"container\\\\\\\",\\\\\\\"width\\\\\\\":1000,\\\\\\\"alignment\\\\\\\":\\\\\\\"0\\\\\\\",\\\\\\\"padding\\\\\\\":\\\\\\\"0,8,0,12\\\\\\\",\\\\\\\"child\\\\\\\":{\\\\\\\"tag\\\\\\\":\\\\\\\"text\\\\\\\",\\\\\\\"data\\\\\\\":\\\\\\\"复制兑换码\\\\\\\",\\\\\\\"style\\\\\\\":{\\\\\\\"color\\\\\\\":\\\\\\\"#198CFE\\\\\\\"}}}}]}}]}\\\",\\\"type\\\":\\\"messageCard\\\"}\",\n" +
            "    \"chat_id\": $chatid\n" +
            "}";

    public static JSONObject getWenJuanString(String tilteName, String textValue, String btUrl) {
        String str = wenJuanString.replace("TilteName", tilteName).replace("TextValue", textValue).replace("BtUrl", btUrl);
        System.out.println(str);
        return JSONObject.parseObject(str);
    }

    public static JSONObject getPrizeString(String textValue, String chatid) {
        String str = NewPrizeString.replace("$CONTENT", textValue).replace("$chatid",chatid);
        System.out.println(str);
        return JSONObject.parseObject(str);
    }


    public static JSONObject getCdkString(String textValue,String cdkCode, String chatid) {
        //String str = NewCDkString.replace("TextValue", textValue).replace("${ExchangeCode}", cdkcode);
        String str = CDkString.replace("$CONTENT",textValue).replace("cdkCode",cdkCode).replace("$chatid",chatid);
        System.out.println(str);
        return JSONObject.parseObject(str);
    }
}
