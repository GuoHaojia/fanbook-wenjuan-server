package com.tduck.cloud.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tduck.cloud.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Ignore;
import org.springframework.data.annotation.Transient;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/18 10:36
 * <p>
 * mark
 */

@Data
@TableName("pr_project_prize_setting")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectPrizeSettingEntity extends BaseEntity<ProjectPrizeSettingEntity> {
    @TableId
    private Long id;

    private Integer type;

    private String projectKey;
    /**
     * 中奖率
     * */
    private Integer probability;
    private Boolean status;

    /**
     * 奖品
     * */

    @TableField(exist = false)
    private List<ProjectPrizeEntity> prizes;
}
