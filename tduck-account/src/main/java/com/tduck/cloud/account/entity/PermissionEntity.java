package com.tduck.cloud.account.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 17:12
 * <p>
 * mark
 */


@Data
@TableName("ac_permission")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionEntity {
    @TableId
    private Long id;
    /**
     * 姓名
     */
    private String title;

    private String action;

    /**
     * 启用停用
     */
    private Integer status;


    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
