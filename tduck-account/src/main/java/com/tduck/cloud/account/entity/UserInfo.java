package com.tduck.cloud.account.entity;

import lombok.Data;

@Data
public class UserInfo {
    Long user_id;
    String nickname;
    Long username;
    String avatar;
    UserToken token;
}
