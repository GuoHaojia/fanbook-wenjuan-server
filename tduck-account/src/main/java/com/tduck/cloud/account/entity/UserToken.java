package com.tduck.cloud.account.entity;

import lombok.Data;

@Data
public class UserToken {
    String access_token;
    String refresh_token;
    String token_type;
    String scope;
}
