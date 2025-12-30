package com.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.health.dto.LoginRequest;
import com.health.dto.RegisterRequest;
import com.health.entity.DoctorInfo;
import com.health.entity.PatientInfo;
import com.health.entity.User;
import com.health.mapper.DoctorInfoMapper;
import com.health.mapper.PatientInfoMapper;
import com.health.mapper.UserMapper;
import com.health.service.AuthService;
import com.health.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserMapper userMapper;
    private final DoctorInfoMapper doctorInfoMapper;
    private final PatientInfoMapper patientInfoMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setStatus(1);
        
        userMapper.insert(user);
        
        // 根据角色创建扩展信息
        if ("DOCTOR".equals(request.getRole())) {
            DoctorInfo doctorInfo = new DoctorInfo();
            doctorInfo.setUserId(user.getId());
            doctorInfo.setDepartment(request.getDepartment());
            doctorInfo.setTitle(request.getTitle());
            doctorInfo.setSpecialty(request.getSpecialty());
            doctorInfo.setHospital(request.getHospital());
            doctorInfo.setLicenseNumber(request.getLicenseNumber());
            doctorInfoMapper.insert(doctorInfo);
        } else if ("PATIENT".equals(request.getRole())) {
            PatientInfo patientInfo = new PatientInfo();
            patientInfo.setUserId(user.getId());
            patientInfo.setBloodType(request.getBloodType());
            patientInfo.setMedicalHistory(request.getMedicalHistory());
            patientInfoMapper.insert(patientInfo);
        }
    }
    
    @Override
    public Map<String, Object> login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 返回用户信息和Token
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return result;
    }
    
    @Override
    public User getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(null);  // 不返回密码
        return user;
    }
    
    @Override
    @Transactional
    public void updateProfile(Long userId, User user) {
        User existUser = userMapper.selectById(userId);
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新允许修改的字段
        existUser.setRealName(user.getRealName());
        existUser.setGender(user.getGender());
        existUser.setAge(user.getAge());
        existUser.setPhone(user.getPhone());
        existUser.setEmail(user.getEmail());
        existUser.setAvatar(user.getAvatar());
        
        userMapper.updateById(existUser);
    }
}
