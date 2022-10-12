package com.tduck.cloud.account.vo;

import lombok.Data;
import org.springframework.data.annotation.Transient;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/9 18:12
 * <p>
 * mark
 * 用户角色接口查询条件
 */

@Data
public class RoleVo {

    private Long id;
    /**
     * 角色id
     */
    private Long roleid;

    private Long userid;
    /**
     * 姓名
     */
    private String rolename;

    //启用禁用
    private Integer status;

}
