package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 患者信息实体类
 */
@Data
@TableName("patient_info")
public class PatientInfo {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private BigDecimal height;
    
    private String bloodType;
    
    private String medicalHistory;
    
    private String allergies;
    
    private String emergencyContact;
    
    private String emergencyPhone;
    
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
