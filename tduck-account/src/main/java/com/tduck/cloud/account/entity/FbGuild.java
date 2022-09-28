package com.tduck.cloud.account.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class FbGuild {
    String name;
    String banner;
    List<JSONObject> channels;
    @JSONField(name = "guild_id")
    String id;
    @JSONField(name = "owner_id")
    String owner;
}
