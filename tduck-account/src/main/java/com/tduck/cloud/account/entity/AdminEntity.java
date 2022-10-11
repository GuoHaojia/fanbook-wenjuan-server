package com.tduck.cloud.account.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tduck.cloud.account.entity.enums.AccountChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 10:31
 * <p>
 * mark
 */
@Data
@TableName("ac_role")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminEntity {
    @TableId
    private Long id;
    /**
     * 姓名
     */
    private String name;

    //启用禁用
    private Integer status;
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
     * 密码
     */
    private String password;

    /**
     * 注册渠道
     */
    private AccountChannelEnum regChannel;
    /**
     * 最后登录渠道
     */
    private AccountChannelEnum lastLoginChannel;
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    /**
     * 最后登录Ip
     */
    private String lastLoginIp;
    /**
     * 状态
     */
    private Boolean deleted;

    /**
     * fb用户长ID
     */
    private String fbUser;
    /**
     * fb用户短ID
     */
    private String fbUsername;


    /**
     * 姓名
     */
    private String rolename;

    //启用禁用
    private Integer rolestatus;

    private Long roleid;

}
