# 智能健康管理平台 - 系统设计文档

## 1. 系统概述

### 1.1 项目名称
智能健康管理平台 (Smart Health Management Platform)

### 1.2 技术栈
- **后端**: Spring Boot 2.7+, MySQL 8.0, MyBatis-Plus, Redis
- **前端**: Vue 3, Element Plus, ECharts, Axios
- **认证**: JWT + Spring Security
- **规则引擎**: Drools 7.x
- **消息推送**: WebSocket
- **定时任务**: Spring Schedule
- **AI集成**: 预留API接口（百度智能云/阿里云）

## 2. 系统架构

### 2.1 总体架构
```
┌─────────────────────────────────────────────────────┐
│                   前端层 (Vue 3)                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │患者门户  │ │医生门户  │ │管理后台  │            │
│  └──────────┘ └──────────┘ └──────────┘            │
└─────────────────────────────────────────────────────┘
                        ↕ HTTP/WebSocket
┌─────────────────────────────────────────────────────┐
│              应用层 (Spring Boot)                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │认证模块  │ │健康管理  │ │医患沟通  │            │
│  ├──────────┤ ├──────────┤ ├──────────┤            │
│  │用药提醒  │ │AI建议    │ │智能预警  │            │
│  └──────────┘ └──────────┘ └──────────┘            │
└─────────────────────────────────────────────────────┘
                        ↕
┌─────────────────────────────────────────────────────┐
│              数据层 (MySQL + Redis)                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │用户数据  │ │健康数据  │ │消息数据  │            │
│  └──────────┘ └──────────┘ └──────────┘            │
└─────────────────────────────────────────────────────┘
```

### 2.2 模块划分
1. **用户认证模块** (auth)
2. **健康数据模块** (health)
3. **数据可视化模块** (visualization)
4. **AI建议模块** (ai)
5. **智能预警模块** (alert)
6. **医患沟通模块** (communication)
7. **用药提醒模块** (medication)

## 3. 数据库设计

### 3.1 用户相关表

#### user - 用户基础表
```sql
CREATE TABLE `user` (
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
```

#### doctor_info - 医生信息表
```sql
CREATE TABLE `doctor_info` (
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
```

#### patient_info - 患者信息表
```sql
CREATE TABLE `patient_info` (
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
```

### 3.2 健康数据表

#### health_record - 健康记录表
```sql
CREATE TABLE `health_record` (
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
```

### 3.3 AI建议与预警表

#### ai_suggestion - AI建议表
```sql
CREATE TABLE `ai_suggestion` (
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
```

#### health_alert - 健康预警表
```sql
CREATE TABLE `health_alert` (
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
```

### 3.4 用药管理表

#### medication - 用药记录表
```sql
CREATE TABLE `medication` (
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
```

#### medication_reminder - 用药提醒记录表
```sql
CREATE TABLE `medication_reminder` (
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
```

### 3.5 医患沟通表

#### doctor_patient_relation - 医患关系表
```sql
CREATE TABLE `doctor_patient_relation` (
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
```

#### message - 消息表
```sql
CREATE TABLE `message` (
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
```

## 4. API接口设计

### 4.1 认证接口
- POST `/api/auth/register` - 用户注册
- POST `/api/auth/login` - 用户登录
- POST `/api/auth/logout` - 用户登出
- GET `/api/auth/info` - 获取当前用户信息
- PUT `/api/auth/profile` - 更新用户信息

### 4.2 健康数据接口
- POST `/api/health/record` - 添加健康记录
- GET `/api/health/record/{id}` - 获取健康记录详情
- GET `/api/health/records` - 获取健康记录列表
- PUT `/api/health/record/{id}` - 更新健康记录
- DELETE `/api/health/record/{id}` - 删除健康记录
- GET `/api/health/statistics` - 获取健康统计数据

### 4.3 数据可视化接口
- GET `/api/visualization/trend` - 获取趋势数据
- GET `/api/visualization/comparison` - 获取对比分析数据
- GET `/api/visualization/distribution` - 获取分布数据

### 4.4 AI建议接口
- POST `/api/ai/analyze` - 分析健康数据并生成建议
- GET `/api/ai/suggestions` - 获取建议列表
- PUT `/api/ai/suggestion/{id}/read` - 标记建议为已读

### 4.5 预警接口
- GET `/api/alert/list` - 获取预警列表
- PUT `/api/alert/{id}/handle` - 处理预警
- GET `/api/alert/rules` - 获取预警规则

### 4.6 用药管理接口
- POST `/api/medication` - 添加用药记录
- GET `/api/medication/{id}` - 获取用药详情
- GET `/api/medication/list` - 获取用药列表
- PUT `/api/medication/{id}` - 更新用药记录
- DELETE `/api/medication/{id}` - 删除用药记录
- POST `/api/medication/reminder/{id}/confirm` - 确认服药

### 4.7 医患沟通接口
- POST `/api/communication/relation` - 建立医患关系
- GET `/api/communication/doctors` - 获取患者的医生列表
- GET `/api/communication/patients` - 获取医生的患者列表
- POST `/api/communication/message` - 发送消息
- GET `/api/communication/messages` - 获取消息列表
- WebSocket `/ws/chat` - 实时聊天

## 5. 前端页面设计

### 5.1 患者端页面
1. **登录/注册页** - 支持患者和医生注册
2. **个人中心** - 个人信息管理
3. **健康数据录入** - 多维度数据录入表单
4. **健康报告** - ECharts图表展示
5. **AI建议** - 个性化健康建议展示
6. **预警中心** - 健康预警列表
7. **用药管理** - 用药记录和提醒
8. **我的医生** - 医患沟通界面

### 5.2 医生端页面
1. **医生工作台** - 患者概览
2. **患者列表** - 管理的患者列表
3. **患者详情** - 查看患者健康数据
4. **预警处理** - 处理患者预警
5. **在线沟通** - 与患者即时沟通
6. **开具处方** - 用药管理

## 6. 技术实现要点

### 6.1 安全认证
- 使用BCrypt加密密码
- JWT Token认证机制
- Spring Security权限控制
- 角色分离(PATIENT, DOCTOR)

### 6.2 智能预警
- Drools规则引擎配置
- 健康指标阈值设定
- 自动触发预警机制
- 多级预警等级

### 6.3 定时任务
- Spring @Scheduled定时扫描
- 用药提醒推送
- 健康报告生成

### 6.4 实时通信
- WebSocket双向通信
- 消息实时推送
- 在线状态管理

### 6.5 数据可视化
- ECharts集成
- 多种图表类型(折线图、柱状图、饼图)
- 响应式设计

## 7. 部署方案

### 7.1 开发环境
- JDK 11+
- Maven 3.6+
- MySQL 8.0
- Redis 6.0
- Node.js 16+

### 7.2 生产环境
- Docker容器化部署
- Nginx反向代理
- MySQL主从复制
- Redis集群

## 8. 优化与扩展

### 8.1 性能优化
- Redis缓存热点数据
- 数据库索引优化
- 分页查询
- 接口限流

### 8.2 扩展功能
- 健康知识库
- 在线问诊
- 健康档案导出
- 数据分享授权
- 家庭成员管理
