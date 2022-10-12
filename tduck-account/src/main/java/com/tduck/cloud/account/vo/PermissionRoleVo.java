package com.tduck.cloud.account.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 17:35
 * <p>
 * mark
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRoleVo {
    /**
     * 权限身份id
     */
    private Long roleid;
    private List<Long> permissionsid;
}
