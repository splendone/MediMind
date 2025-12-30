package com.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.health.entity.HealthRecord;
import com.health.service.HealthRecordService;
import com.health.utils.JwtUtil;
import com.health.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 健康记录控制器
 */
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@CrossOrigin
public class HealthRecordController {
    
    private final HealthRecordService healthRecordService;
    private final JwtUtil jwtUtil;
    
    /**
     * 添加健康记录
     */
    @PostMapping("/record")
    public Result<Void> addRecord(@RequestBody HealthRecord record, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        record.setUserId(userId);
        healthRecordService.addRecord(record);
        return Result.success("添加成功", null);
    }
    
    /**
     * 获取健康记录详情
     */
    @GetMapping("/record/{id}")
    public Result<HealthRecord> getRecord(@PathVariable Long id) {
        HealthRecord record = healthRecordService.getRecordById(id);
        return Result.success(record);
    }
    
    /**
     * 分页查询健康记录
     */
    @GetMapping("/records")
    public Result<Page<HealthRecord>> getRecordList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Page<HealthRecord> page = healthRecordService.getRecordList(userId, pageNum, pageSize, startDate, endDate);
        return Result.success(page);
    }
    
    /**
     * 更新健康记录
     */
    @PutMapping("/record/{id}")
    public Result<Void> updateRecord(@PathVariable Long id, @RequestBody HealthRecord record, 
                                      HttpServletRequest request) {
        getUserIdFromRequest(request);  // 验证权限
        record.setId(id);
        healthRecordService.updateRecord(record);
        return Result.success("更新成功", null);
    }
    
    /**
     * 删除健康记录
     */
    @DeleteMapping("/record/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id, HttpServletRequest request) {
        getUserIdFromRequest(request);  // 验证权限
        healthRecordService.deleteRecord(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 获取健康统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam(defaultValue = "30") Integer days,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        Map<String, Object> statistics = healthRecordService.getStatistics(userId, days);
        return Result.success(statistics);
    }
    
    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getTrendData(
            @RequestParam String indicator,
            @RequestParam(defaultValue = "30") Integer days,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Map<String, Object>> trendData = healthRecordService.getTrendData(userId, indicator, days);
        return Result.success(trendData);
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("未授权访问");
    }
}
