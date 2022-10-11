package com.tduck.cloud.account.vo;

import lombok.Data;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 14:54
 * <p>
 * mark
 */


@Data
public class AdminVo {
    private Long id;
    /**
     * 权限id
     */
    private Long roleid;

    private Long userid;
    /**
     * 权限名
     */
    private String rolename;

    //启用禁用
    private Integer status;
}
