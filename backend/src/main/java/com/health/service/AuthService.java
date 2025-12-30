package com.health.service;

import com.health.dto.LoginRequest;
import com.health.dto.RegisterRequest;
import com.health.entity.User;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户注册
     */
    void register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    Map<String, Object> login(LoginRequest request);
    
    /**
     * 获取当前用户信息
     */
    User getCurrentUser(Long userId);
    
    /**
     * 更新用户信息
     */
    void updateProfile(Long userId, User user);
}
