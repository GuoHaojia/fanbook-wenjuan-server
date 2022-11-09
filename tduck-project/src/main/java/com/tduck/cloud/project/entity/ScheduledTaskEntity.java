package com.tduck.cloud.project.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tduck.cloud.common.entity.BaseEntity;
import com.tduck.cloud.common.mybatis.handler.JacksonTypeHandler;
import lombok.*;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "scheduled_task", autoResultMap = true)
@Accessors(chain = true)
public class ScheduledTaskEntity extends BaseEntity<ScheduledTaskEntity> {

    @TableId
    private Long id;

    @TableField("`key`")
    @NotNull(message = "key请求异常")
    private String key;

    @NotNull(message = "时间请求异常")
    private LocalDateTime time;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> param;

    private int type;

    private int status;

    private int Flag;

    private Long pid;

}
