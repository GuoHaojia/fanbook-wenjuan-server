package com.tduck.cloud.project.entity;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tduck.cloud.common.entity.BaseEntity;
import com.tduck.cloud.common.mybatis.handler.JacksonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 项目表单项(ProjectResult)表实体类
 *
 * @author smalljop
 * @since 2020-11-23 14:09:20
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName(value = "pr_user_project_result", autoResultMap = true)
//不允许被序列化 允许被反序列化
@JsonIgnoreProperties(value = {UserProjectResultEntity.Fields.originalData}, allowSetters = true)
public class UserProjectResultEntity extends BaseEntity<UserProjectResultEntity> {
    /**
     *
     */
    @TableId
    private Long id;
    /**
     * 项目key
     */
    @NotBlank(message = "错误请求")
    private String projectKey;


    private Long serialNumber;

    /**
     * 填写结果原始数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> originalData;


    /**
     * 填写结果处理后数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> processData;

    /**
     * 填写用户Ua
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> submitUa;

    /**
     * 提交系统
     */
    private String submitOs;


    /**
     * 提交浏览器
     */
    private String submitBrowser;

    /**
     * 提交ip
     */
    private String submitRequestIp;


    /**
     * 提交ip
     */
    private String submitAddress;

    /**
     * 完成时间
     */
    private Long completeTime;

    /**
     * 微信openID
     */
    private String wxOpenId;


    /**
     * FB用户长ID
     */
    private Long fbUserid;
    /**
     * FB用户短ID
     */
    private String fbUsername;
    /**
     * FB用户短ID
     */
    @TableField(exist = false)
    private String fbNickname;
    /**
     * FB服务器ID
     */
    private Long guildId;
    /**
     * FB服务器名称
     */
    private String guildName;
    /**
     * 微信用户信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> wxUserInfo;

    private String publishTime;

    private String chatId;


}