package com.tduck.cloud.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 17:47
 * <p>
 * mark
 */



@Data
@TableName("ac_role_permission")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRoleEntity {
    private Long roleId;
    private Long permissionId;
    private LocalDateTime createTime;
}
