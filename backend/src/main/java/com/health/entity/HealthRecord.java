package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 健康记录实体类
 */
@Data
@TableName("health_record")
public class HealthRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime recordTime;
    
    private Integer systolicPressure;  // 收缩压
    
    private Integer diastolicPressure;  // 舒张压
    
    private Integer heartRate;  // 心率
    
    private BigDecimal bloodSugar;  // 血糖
    
    private BigDecimal weight;  // 体重
    
    private BigDecimal temperature;  // 体温
    
    private Integer bloodOxygen;  // 血氧
    
    private Integer steps;  // 步数
    
    private BigDecimal sleepHours;  // 睡眠时长
    
    private String notes;
    
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
