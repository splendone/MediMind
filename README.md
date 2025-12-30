# 智能健康管理平台

## 项目简介

智能健康管理平台是一个基于Spring Boot + Vue.js构建的前后端分离Web应用，旨在为患者和医生提供全面的健康数据管理、智能分析和医患沟通服务。

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.3.1
- **缓存**: Redis
- **安全**: Spring Security + JWT
- **规则引擎**: Drools 7.73.0
- **消息通信**: WebSocket
- **工具类**: Hutool 5.8.22

### 前端
- **框架**: Vue 3
- **UI组件**: Element Plus
- **图表**: ECharts 5.x
- **HTTP客户端**: Axios
- **路由**: Vue Router
- **状态管理**: Pinia

## 核心功能

### 1. 用户认证系统
- ✅ 双角色注册登录（患者端和医生端）
- ✅ JWT Token认证机制
- ✅ 用户信息管理
- ✅ 角色权限控制

### 2. 健康数据管理
- ✅ 多维度健康数据录入（血压、血糖、心率、体重等）
- ✅ 健康记录CRUD操作
- ✅ 数据分页查询
- ✅ 健康统计分析

### 3. 数据可视化
- 📊 ECharts趋势图表
- 📊 健康指标对比分析
- 📊 数据分布展示
- 📊 时间序列可视化

### 4. AI健康建议
- 🤖 基于健康数据的智能分析
- 🤖 个性化健康建议生成
- 🤖 建议优先级分类
- 🤖 预留第三方AI API接口

### 5. 智能预警系统
- ⚠️ Drools规则引擎驱动
- ⚠️ 多级预警机制（轻度、中度、重度）
- ⚠️ 实时健康指标监控
- ⚠️ 自动触发预警通知

### 6. 医患沟通
- 💬 WebSocket实时聊天
- 💬 医患关系管理
- 💬 消息状态追踪
- 💬 文件共享支持

### 7. 用药管理
- 💊 用药记录管理
- 💊 定时用药提醒
- 💊 服药状态追踪
- 💊 用药历史查询

## 项目结构

```
smart-health-platform/
├── backend/                    # 后端项目
│   ├── src/main/
│   │   ├── java/com/health/
│   │   │   ├── config/        # 配置类
│   │   │   ├── controller/    # 控制器
│   │   │   ├── entity/        # 实体类
│   │   │   ├── mapper/        # Mapper接口
│   │   │   ├── service/       # 服务层
│   │   │   ├── dto/           # 数据传输对象
│   │   │   ├── vo/            # 视图对象
│   │   │   ├── utils/         # 工具类
│   │   │   ├── websocket/     # WebSocket处理
│   │   │   └── drools/        # Drools规则服务
│   │   └── resources/
│   │       ├── drools/rules/  # Drools规则文件
│   │       ├── mapper/        # MyBatis映射文件
│   │       ├── application.yml
│   │       └── schema.sql     # 数据库初始化脚本
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/             # 页面组件
│   │   ├── components/        # 公共组件
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   ├── api/               # API接口
│   │   └── utils/             # 工具函数
│   └── package.json
├── SYSTEM_DESIGN.md           # 系统设计文档
└── README.md                  # 项目说明文档
```

## 数据库设计

### 核心表结构
- `user` - 用户基础表
- `doctor_info` - 医生信息表
- `patient_info` - 患者信息表
- `health_record` - 健康记录表
- `health_alert` - 健康预警表
- `ai_suggestion` - AI建议表
- `medication` - 用药记录表
- `medication_reminder` - 用药提醒表
- `doctor_patient_relation` - 医患关系表
- `message` - 消息表

详细设计请参考 [SYSTEM_DESIGN.md](./SYSTEM_DESIGN.md)

## 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+

### 后端启动

1. 创建数据库并执行初始化脚本
```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

2. 修改配置文件
```bash
vim backend/src/main/resources/application.yml
# 修改数据库连接信息、Redis配置等
```

3. 编译并运行
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080/api 启动

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端应用将在 http://localhost:3000 启动

## API接口文档

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/info` - 获取当前用户信息
- `PUT /api/auth/profile` - 更新用户信息

### 健康数据接口
- `POST /api/health/record` - 添加健康记录
- `GET /api/health/records` - 查询健康记录列表
- `GET /api/health/record/{id}` - 获取健康记录详情
- `PUT /api/health/record/{id}` - 更新健康记录
- `DELETE /api/health/record/{id}` - 删除健康记录
- `GET /api/health/statistics` - 获取健康统计
- `GET /api/health/trend` - 获取趋势数据

### WebSocket接口
- `ws://localhost:8080/api/ws/chat` - 实时聊天WebSocket端点

## 测试账号

系统已预置以下测试账号：

**患者账号：**
- 用户名: patient1
- 密码: password123
- 角色: PATIENT

**医生账号：**
- 用户名: doctor1
- 密码: password123
- 角色: DOCTOR

## 智能预警规则

系统使用Drools规则引擎实现健康预警，当前支持的预警规则包括：

- **高血压预警**（3级）
  - 轻度：收缩压 ≥ 140 或舒张压 ≥ 90
  - 中度：收缩压 ≥ 160 或舒张压 ≥ 100
  - 重度：收缩压 ≥ 180 或舒张压 ≥ 110

- **低血压预警**（2级）
  - 收缩压 < 90 或舒张压 < 60

- **心率异常预警**（2级）
  - 心率过快：> 100次/分
  - 心率过慢：< 60次/分

- **血糖异常预警**（3级）
  - 高血糖：≥ 7.0 mmol/L
  - 低血糖：< 3.9 mmol/L

## 扩展功能

### 计划中的功能
- [ ] 健康知识库
- [ ] 在线视频问诊
- [ ] 健康档案导出（PDF）
- [ ] 数据分享授权机制
- [ ] 家庭成员健康管理
- [ ] 运动计划管理
- [ ] 饮食建议系统

## 部署说明

### Docker部署

```bash
# 构建后端镜像
cd backend
docker build -t smart-health-backend .

# 构建前端镜像
cd ../frontend
docker build -t smart-health-frontend .

# 使用docker-compose启动
docker-compose up -d
```

### 生产环境配置
- 使用Nginx作为反向代理
- 配置MySQL主从复制
- 部署Redis集群
- 启用HTTPS证书

## 贡献指南

欢迎提交Issue和Pull Request来帮助改进这个项目。

## 许可证

MIT License

## 联系方式

如有问题或建议，请联系项目维护者。

---

**注意**: 本系统为演示项目，AI健康建议功能需要集成第三方API（如百度智能云或阿里云健康API）才能完整使用。当前版本提供了接口预留，可根据实际需求进行集成。
