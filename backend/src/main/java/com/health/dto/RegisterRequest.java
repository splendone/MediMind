package com.health.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名必须是4-20位字母、数字或下划线")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{6,20}$", message = "密码必须包含字母和数字，长度6-20位")
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    private Integer gender;
    
    private Integer age;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(PATIENT|DOCTOR)$", message = "角色只能是PATIENT或DOCTOR")
    private String role;
    
    // 医生专用字段
    private String department;
    private String title;
    private String specialty;
    private String hospital;
    private String licenseNumber;
    
    // 患者专用字段
    private String bloodType;
    private String medicalHistory;
}
