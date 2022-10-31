package com.tduck.cloud.account.vo;

import lombok.Data;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/29 18:11
 * <p>
 * mark
 */
@Data
public class Chat {
    Long id;
    Long guild_id;
    String type;
    Integer channel_type;
}
