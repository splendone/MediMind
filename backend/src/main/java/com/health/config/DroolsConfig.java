package com.health.config;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * Drools规则引擎配置类
 */
@Configuration
public class DroolsConfig {
    
    private static final String RULES_PATH = "drools/rules/";
    
    @Bean
    public KieContainer kieContainer() throws IOException {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        
        // 加载规则文件
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:" + RULES_PATH + "**/*.drl");
        
        for (Resource resource : resources) {
            kieFileSystem.write(ResourceFactory.newClassPathResource(
                    RULES_PATH + resource.getFilename(), "UTF-8"));
        }
        
        // 构建
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        
        // 检查错误
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException("Drools规则加载失败: " + results.getMessages());
        }
        
        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }
}
