package com.tduck.cloud.account.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/10 10:43
 * <p>
 * mark
 */
@Data
public class UserRoleVo {

    private Long id;

    private Long userid;
    /**
     * 姓名
     */
    private String name;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 绑定qq
     */
    private String qqName;

    /**
     * 绑定微信名称
     */
    private String wxName;

    /**
     * 姓名
     */
    private String rolename;

    //启用禁用
    private Integer rolestatus;

    private Long roleid;
}
