package com.tduck.cloud.account.entity;

import lombok.Data;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/13 11:01
 * <p>
 * mark
 */
@Data
public class MemberInfo {
    String id;
    String first_name;
    String username;
    String avatar;
    Boolean is_bot;
}
