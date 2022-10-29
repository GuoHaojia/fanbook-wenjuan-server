package com.tduck.cloud.account.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/12 18:33
 * <p>
 * mark
 */
@Data
public class FanbookRole {
    Long id;
    String name;
    Long permissions;
    Boolean mentionable;
    Boolean hoist;
    Long color;
    Integer position;
    Integer member_count;
    Bot tag;

    @Data
    class Bot{
        Long botId;
    }
}
