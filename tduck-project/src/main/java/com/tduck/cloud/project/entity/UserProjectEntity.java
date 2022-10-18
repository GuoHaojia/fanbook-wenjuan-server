package com.tduck.cloud.project.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tduck.cloud.common.entity.BaseEntity;
import com.tduck.cloud.common.validator.group.AddGroup;
import com.tduck.cloud.common.validator.group.UpdateGroup;
import com.tduck.cloud.project.entity.enums.ProjectSourceTypeEnum;
import com.tduck.cloud.project.entity.enums.ProjectStatusEnum;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户项目表(Project)表实体类
 *
 * @author smalljop
 * @since 2020-11-18 18:16:16
 */
@Data
@TableName("pr_user_project")
@FieldNameConstants
public class UserProjectEntity extends BaseEntity<UserProjectEntity> {
    @TableId
    private Long id;
    /**
     * 项目code
     */
    @NotBlank(message = "错误请求", groups = {UpdateGroup.class})
    @TableField("`key`")
    private String key;
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String name;
    /**
     * 项目描述
     */
    @TableField("`describe`")
    private String describe;


    /**
     * 项目来源
     */
    private ProjectSourceTypeEnum sourceType;

    /**
     * 来源ID
     */
    private String sourceId;

    /**
     * 用户ID
     */
    private Long userId;

    /***
     * 状态
     */
    @TableField("`status`")
    private ProjectStatusEnum status;
    /**
     * 项目类型
     */
    private Integer type;

    @TableField(value = "is_deleted")
    private Boolean deleted;


    @TableField(value = "guild_id")
    private Long guildId;


    @TableField(value = "fb_user")
    private Long fbUser;

    /**
     * 答卷数量
     */
    @TableField(value = "answer_num")
    private Integer answerNum;

    /**
     * 发布次数
     */
    @TableField(value = "publish_num")
    private Integer publishNum;

    //    @TableField(exist = false)
//    private Long fbChannel;
//    @TableField(exist = false)
//    private String guildName;
//    @TableField(exist = false)
//    private String fbChannelName;
    /**
     * 推送列表
     */
    @TableField(exist = false)
    List<PublishEntity> publishList;

    @TableField(exist = false)
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_MINUTE_PATTERN)
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    LocalDateTime publishTime;
}