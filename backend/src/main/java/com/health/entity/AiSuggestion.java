package com.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI建议实体类
 */
@Data
@TableName("ai_suggestion")
public class AiSuggestion {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long healthRecordId;
    
    private String suggestionType;
    
    private String content;
    
    private Integer priority;  // 1-低 2-中 3-高
    
    private Integer status;  // 0-未读 1-已读
    
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
