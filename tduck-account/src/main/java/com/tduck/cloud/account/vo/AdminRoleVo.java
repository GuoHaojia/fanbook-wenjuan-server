package com.tduck.cloud.account.vo;

import lombok.Data;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 15:58
 * <p>
 * mark
 */
@Data
public class AdminRoleVo {
    private Long id;

    private Long userid;
    /**
     * 姓名
     */
    private String name;

    /**
     * 姓名
     */
    private String rolename;

    //启用禁用
    private Integer rolestatus;

    private Long roleid;
}
