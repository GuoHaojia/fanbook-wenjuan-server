package com.tduck.cloud.account.entity;

import lombok.Data;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/12 17:40
 * <p>
 * mark
 */

@Data
public class BotInfo {
    /***
     *
     * fb_id
     */
    Long id;
    Boolean is_bot;
    String first_name;
    String last_name;
    String username;
    String avatar;
    String user_token;
    Long owner_id;
    Boolean can_join_groups;
    Boolean can_read_all_group_messages;
    Boolean supports_inline_queries;
}
