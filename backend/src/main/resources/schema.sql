-- 智能健康管理平台数据库初始化脚本

CREATE DATABASE IF NOT EXISTS smart_health DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smart_health;

-- 用户基础表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(加密)',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `gender` TINYINT COMMENT '性别 0-女 1-男',
  `age` INT COMMENT '年龄',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `role` VARCHAR(20) NOT NULL COMMENT '角色: PATIENT, DOCTOR',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_username (`username`),
  INDEX idx_role (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 医生信息表
CREATE TABLE IF NOT EXISTS `doctor_info` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT UNIQUE NOT NULL COMMENT '用户ID',
  `department` VARCHAR(50) COMMENT '科室',
  `title` VARCHAR(50) COMMENT '职称',
  `specialty` VARCHAR(200) COMMENT '专长',
  `hospital` VARCHAR(100) COMMENT '医院',
  `license_number` VARCHAR(50) COMMENT '执业证书号',
  `introduction` TEXT COMMENT '简介',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生信息表';

-- 患者信息表
CREATE TABLE IF NOT EXISTS `patient_info` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT UNIQUE NOT NULL COMMENT '用户ID',
  `height` DECIMAL(5,2) COMMENT '身高(cm)',
  `blood_type` VARCHAR(10) COMMENT '血型',
  `medical_history` TEXT COMMENT '病史',
  `allergies` TEXT COMMENT '过敏史',
  `emergency_contact` VARCHAR(50) COMMENT '紧急联系人',
  `emergency_phone` VARCHAR(20) COMMENT '紧急联系电话',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信息表';

-- 健康记录表
CREATE TABLE IF NOT EXISTS `health_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `record_time` TIME COMMENT '记录时间',
  `systolic_pressure` INT COMMENT '收缩压(mmHg)',
  `diastolic_pressure` INT COMMENT '舒张压(mmHg)',
  `heart_rate` INT COMMENT '心率(次/分)',
  `blood_sugar` DECIMAL(5,2) COMMENT '血糖(mmol/L)',
  `weight` DECIMAL(5,2) COMMENT '体重(kg)',
  `temperature` DECIMAL(4,2) COMMENT '体温(°C)',
  `blood_oxygen` INT COMMENT '血氧(%)',
  `steps` INT COMMENT '步数',
  `sleep_hours` DECIMAL(4,2) COMMENT '睡眠时长(小时)',
  `notes` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_date (`user_id`, `record_date`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录表';

-- AI建议表
CREATE TABLE IF NOT EXISTS `ai_suggestion` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `health_record_id` BIGINT COMMENT '关联的健康记录ID',
  `suggestion_type` VARCHAR(50) COMMENT '建议类型',
  `content` TEXT COMMENT '建议内容',
  `priority` TINYINT COMMENT '优先级 1-低 2-中 3-高',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-未读 1-已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_status (`user_id`, `status`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI建议表';

-- 健康预警表
CREATE TABLE IF NOT EXISTS `health_alert` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `health_record_id` BIGINT COMMENT '关联的健康记录ID',
  `alert_type` VARCHAR(50) COMMENT '预警类型',
  `alert_level` TINYINT COMMENT '预警级别 1-轻度 2-中度 3-重度',
  `indicator` VARCHAR(50) COMMENT '指标名称',
  `value` VARCHAR(50) COMMENT '指标值',
  `threshold` VARCHAR(50) COMMENT '阈值',
  `message` TEXT COMMENT '预警信息',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-未处理 1-已处理',
  `handled_by` BIGINT COMMENT '处理人ID',
  `handle_time` DATETIME COMMENT '处理时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user_status (`user_id`, `status`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康预警表';

-- 用药记录表
CREATE TABLE IF NOT EXISTS `medication` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `doctor_id` BIGINT COMMENT '开具医生ID',
  `medicine_name` VARCHAR(100) NOT NULL COMMENT '药品名称',
  `dosage` VARCHAR(50) COMMENT '剂量',
  `frequency` VARCHAR(50) COMMENT '频率',
  `start_date` DATE COMMENT '开始日期',
  `end_date` DATE COMMENT '结束日期',
  `remind_times` VARCHAR(100) COMMENT '提醒时间(JSON数组)',
  `notes` TEXT COMMENT '备注',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-已停药 1-用药中',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_status (`user_id`, `status`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药记录表';

-- 用药提醒记录表
CREATE TABLE IF NOT EXISTS `medication_reminder` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `medication_id` BIGINT NOT NULL COMMENT '用药ID',
  `remind_date` DATE NOT NULL COMMENT '提醒日期',
  `remind_time` TIME NOT NULL COMMENT '提醒时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-待提醒 1-已提醒 2-已服药 3-已忽略',
  `take_time` DATETIME COMMENT '服药时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_medication_date (`medication_id`, `remind_date`),
  INDEX idx_status (`status`, `remind_date`, `remind_time`),
  FOREIGN KEY (`medication_id`) REFERENCES `medication`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药提醒记录表';

-- 医患关系表
CREATE TABLE IF NOT EXISTS `doctor_patient_relation` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-已解除 1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_doctor_patient (`doctor_id`, `patient_id`),
  INDEX idx_patient (`patient_id`),
  FOREIGN KEY (`doctor_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`patient_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医患关系表';

-- 消息表
CREATE TABLE IF NOT EXISTS `message` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `from_user_id` BIGINT NOT NULL COMMENT '发送者ID',
  `to_user_id` BIGINT NOT NULL COMMENT '接收者ID',
  `content` TEXT COMMENT '消息内容',
  `message_type` TINYINT DEFAULT 1 COMMENT '消息类型 1-文本 2-图片 3-文件',
  `file_url` VARCHAR(255) COMMENT '文件URL',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0-未读 1-已读',
  `read_time` DATETIME COMMENT '读取时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_to_user_status (`to_user_id`, `status`),
  INDEX idx_conversation (`from_user_id`, `to_user_id`, `create_time`),
  FOREIGN KEY (`from_user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`to_user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 插入测试数据
-- 密码都是: password123 (BCrypt加密后)
INSERT INTO `user` (`username`, `password`, `real_name`, `gender`, `age`, `phone`, `email`, `role`, `status`) VALUES
('patient1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', 1, 35, '13800138001', 'patient1@example.com', 'PATIENT', 1),
('patient2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', 0, 28, '13800138002', 'patient2@example.com', 'PATIENT', 1),
('doctor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王医生', 1, 45, '13800138003', 'doctor1@example.com', 'DOCTOR', 1),
('doctor2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵医生', 0, 38, '13800138004', 'doctor2@example.com', 'DOCTOR', 1);

INSERT INTO `patient_info` (`user_id`, `height`, `blood_type`, `medical_history`, `allergies`) VALUES
(1, 175.00, 'A', '高血压病史3年', '青霉素过敏'),
(2, 165.00, 'O', '无', '无');

INSERT INTO `doctor_info` (`user_id`, `department`, `title`, `specialty`, `hospital`, `license_number`) VALUES
(3, '心内科', '主任医师', '心血管疾病诊治', '市人民医院', 'D12345678'),
(4, '内分泌科', '副主任医师', '糖尿病、甲状腺疾病', '市人民医院', 'D87654321');
