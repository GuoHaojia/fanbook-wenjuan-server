package com.tduck.cloud.account.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/13 14:39
 * <p>
 * mark
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FanbookRoleVo {
    String token;
    /*
    * 服务器id
    * */

    String guildId;
    String roleId;
}
