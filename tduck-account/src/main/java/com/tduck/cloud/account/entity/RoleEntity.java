package com.tduck.cloud.account.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tduck.cloud.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/9 16:10
 * <p>
 * mark
 *
 * 用户角色表
 *
 */

@Data
@TableName("pr_role")
@Builder
public class RoleEntity extends BaseEntity<RoleEntity> {
    @TableId
    private Long id;
    /**
     * 姓名
     */
    private String name;

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
