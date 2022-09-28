package com.tduck.cloud.common.constant;


import com.alibaba.fastjson.JSONObject;

public class FanbookCard {
    static String wenJuanString = "{\"type\":\"column\",\"children\":[{\"type\":\"ic_title\",\"param\":{\"text\":\"TilteName\",\"icon\":\"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fstatic.haoapp.mobi%2Ftrochili%2F88%2F8c%2F888cbad498d8686a2432b422cc1cbba868421bb7-356d7a56d100830c0b187bcd45260c8ad6dd41aa.png&refer=http%3A%2F%2Fstatic.haoapp.mobi&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1650093049&t=ac62ba764c5ebdec4a75fd39f3f1cb68\",\"bg\":0,\"line\":0,\"status\":0}},{\"type\":\"text\",\"param\":{\"text\":\"TextValue\",\"type\":0,\"line\":0}},{\"type\":\"button\",\"param\":{\"list\":[{\"type\":1,\"text\":\"填写问卷\",\"enable\":1,\"event\":{\"method\":\"mini_program\",\"param\":{\"appId\":\"BtUrl\"}}}]}}]}";

    public static JSONObject getWenJuanString(String tilteName, String textValue, String btUrl) {
        String str = wenJuanString.replace("TilteName", tilteName).replace("TextValue", textValue).replace("BtUrl", btUrl);
        System.out.println(str);
        return JSONObject.parseObject(str);
    }
}
