package com.tduck.cloud.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/13 15:34
 * <p>
 * mark
 */
@Data
@TableName("ac_user_role")
@Builder
public class AdminRoleEntity {
    Long userId;
    Long roleId;
    int status;
    LocalDateTime createTime;
}
