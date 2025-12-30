package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康预警实体类
 */
@Data
@TableName("health_alert")
public class HealthAlert {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long healthRecordId;
    
    private String alertType;
    
    private Integer alertLevel;  // 1-轻度 2-中度 3-重度
    
    private String indicator;  // 指标名称
    
    private String value;  // 指标值
    
    private String threshold;  // 阈值
    
    private String message;
    
    private Integer status;  // 0-未处理 1-已处理
    
    private Long handledBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;
    
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
