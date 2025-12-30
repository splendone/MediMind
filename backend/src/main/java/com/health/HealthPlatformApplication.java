package com.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 智能健康管理平台启动类
 *
 * @author SmartHealth Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.health.mapper")
@EnableScheduling
public class HealthPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthPlatformApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  智能健康管理平台启动成功！");
        System.out.println("  API地址: http://localhost:8080/api");
        System.out.println("==============================================");
    }
}
