package com.tduck.cloud.project.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tduck.cloud.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/18 10:44
 * <p>
 * mark
 */

@Data
@TableName("pr_project_prize_item")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectPrizeItemEntity extends BaseEntity<ProjectPrizeItemEntity> {
    @TableId
    private Long id;

    private Integer type;

    private String projectKey;

    private String fanbookid;

    private String nickname;

    private String phoneNumber;

    private Boolean status;

    private LocalDateTime getTime;

    private String prize;

    private Long prizeid;
}
