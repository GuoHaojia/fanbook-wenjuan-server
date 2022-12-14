package com.tduck.cloud.project.entity;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tduck.cloud.common.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Data
@TableName("pr_user_publish")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishEntity extends BaseEntity<PublishEntity> {
    @TableId(type = IdType.AUTO)
    Long id;
    @TableField("`key`")
    String key;
    @JsonProperty(value = "guild_id")
    Long guildId;
    @JsonProperty(value = "guild_name")
    String guildName;
    @JsonProperty(value = "channel_id")
    Long fbChannel;
    @JsonProperty(value = "name")
    String fbChannelName;
    @JsonProperty(value = "status")
    Integer status;
    @TableField("`publish_time`")
    String publishTime;
    @TableField("`answer_num`")
    Integer answerNum;


}
