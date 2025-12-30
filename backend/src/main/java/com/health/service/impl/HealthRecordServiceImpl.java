package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.entity.HealthRecord;
import com.health.mapper.HealthRecordMapper;
import com.health.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 健康记录服务实现类
 */
@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl implements HealthRecordService {
    
    private final HealthRecordMapper healthRecordMapper;
    
    @Override
    public void addRecord(HealthRecord record) {
        if (record.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDate.now());
        }
        healthRecordMapper.insert(record);
    }
    
    @Override
    public HealthRecord getRecordById(Long id) {
        HealthRecord record = healthRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("健康记录不存在");
        }
        return record;
    }
    
    @Override
    public Page<HealthRecord> getRecordList(Long userId, Integer pageNum, Integer pageSize,
                                             LocalDate startDate, LocalDate endDate) {
        Page<HealthRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getUserId, userId);
        
        if (startDate != null) {
            wrapper.ge(HealthRecord::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(HealthRecord::getRecordDate, endDate);
        }
        
        wrapper.orderByDesc(HealthRecord::getRecordDate, HealthRecord::getRecordTime);
        
        return healthRecordMapper.selectPage(page, wrapper);
    }
    
    @Override
    public void updateRecord(HealthRecord record) {
        if (healthRecordMapper.selectById(record.getId()) == null) {
            throw new RuntimeException("健康记录不存在");
        }
        healthRecordMapper.updateById(record);
    }
    
    @Override
    public void deleteRecord(Long id) {
        healthRecordMapper.deleteById(id);
    }
    
    @Override
    public Map<String, Object> getStatistics(Long userId, Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getUserId, userId)
               .ge(HealthRecord::getRecordDate, startDate)
               .le(HealthRecord::getRecordDate, endDate);
        
        List<HealthRecord> records = healthRecordMapper.selectList(wrapper);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRecords", records.size());
        
        if (!records.isEmpty()) {
            // 计算平均值
            statistics.put("avgSystolicPressure", calculateAverage(records, r -> r.getSystolicPressure()));
            statistics.put("avgDiastolicPressure", calculateAverage(records, r -> r.getDiastolicPressure()));
            statistics.put("avgHeartRate", calculateAverage(records, r -> r.getHeartRate()));
            statistics.put("avgWeight", calculateAverage(records, r -> r.getWeight()));
            statistics.put("avgSteps", calculateAverage(records, r -> r.getSteps()));
        }
        
        return statistics;
    }
    
    @Override
    public List<Map<String, Object>> getTrendData(Long userId, String indicator, Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getUserId, userId)
               .ge(HealthRecord::getRecordDate, startDate)
               .le(HealthRecord::getRecordDate, endDate)
               .orderByAsc(HealthRecord::getRecordDate);
        
        List<HealthRecord> records = healthRecordMapper.selectList(wrapper);
        
        return records.stream().map(record -> {
            Map<String, Object> data = new HashMap<>();
            data.put("date", record.getRecordDate());
            
            switch (indicator) {
                case "bloodPressure":
                    data.put("systolic", record.getSystolicPressure());
                    data.put("diastolic", record.getDiastolicPressure());
                    break;
                case "heartRate":
                    data.put("value", record.getHeartRate());
                    break;
                case "bloodSugar":
                    data.put("value", record.getBloodSugar());
                    break;
                case "weight":
                    data.put("value", record.getWeight());
                    break;
                case "steps":
                    data.put("value", record.getSteps());
                    break;
                default:
                    break;
            }
            
            return data;
        }).collect(Collectors.toList());
    }
    
    private Double calculateAverage(List<HealthRecord> records, java.util.function.Function<HealthRecord, Number> extractor) {
        return records.stream()
                      .map(extractor)
                      .filter(Objects::nonNull)
                      .mapToDouble(Number::doubleValue)
                      .average()
                      .orElse(0.0);
    }
}
