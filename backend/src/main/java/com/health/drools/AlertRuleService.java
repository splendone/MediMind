package com.health.drools;

import com.health.entity.HealthAlert;
import com.health.entity.HealthRecord;
import com.health.mapper.HealthAlertMapper;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 预警规则服务
 */
@Service
@RequiredArgsConstructor
public class AlertRuleService {
    
    private final KieContainer kieContainer;
    private final HealthAlertMapper healthAlertMapper;
    
    /**
     * 执行健康预警规则检查
     */
    public List<HealthAlert> executeHealthAlertRules(HealthRecord record) {
        KieSession kieSession = kieContainer.newKieSession();
        List<HealthAlert> alerts = new ArrayList<>();
        
        try {
            // 插入健康记录事实
            kieSession.insert(record);
            
            // 设置全局变量收集预警
            kieSession.setGlobal("alerts", alerts);
            
            // 触发规则
            kieSession.fireAllRules();
            
            // 保存预警到数据库
            for (HealthAlert alert : alerts) {
                healthAlertMapper.insert(alert);
            }
            
        } finally {
            kieSession.dispose();
        }
        
        return alerts;
    }
}
