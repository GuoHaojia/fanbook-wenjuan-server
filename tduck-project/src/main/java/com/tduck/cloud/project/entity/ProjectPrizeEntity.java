package com.tduck.cloud.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tduck.cloud.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/18 10:30
 * <p>
 * mark
 */

@Data
@TableName("pr_project_prize")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectPrizeEntity extends BaseEntity<ProjectPrizeEntity> {
    @TableId
    private Long id;

    private Integer type;

    private String projectKey;
    private Integer count;
    @TableField("`desc`")
    private String desc;
    @TableField("`status`")
    private Boolean status;
}
