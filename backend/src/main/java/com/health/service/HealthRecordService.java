package com.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.entity.HealthRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 健康记录服务接口
 */
public interface HealthRecordService {
    
    /**
     * 添加健康记录
     */
    void addRecord(HealthRecord record);
    
    /**
     * 获取健康记录详情
     */
    HealthRecord getRecordById(Long id);
    
    /**
     * 分页查询健康记录
     */
    Page<HealthRecord> getRecordList(Long userId, Integer pageNum, Integer pageSize, 
                                      LocalDate startDate, LocalDate endDate);
    
    /**
     * 更新健康记录
     */
    void updateRecord(HealthRecord record);
    
    /**
     * 删除健康记录
     */
    void deleteRecord(Long id);
    
    /**
     * 获取健康统计数据
     */
    Map<String, Object> getStatistics(Long userId, Integer days);
    
    /**
     * 获取趋势数据
     */
    List<Map<String, Object>> getTrendData(Long userId, String indicator, Integer days);
}
