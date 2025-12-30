# 智能健康管理平台 - 部署指南

## 环境准备

### 必需软件
- **JDK**: 11 或更高版本
- **Maven**: 3.6+ 
- **MySQL**: 8.0+
- **Redis**: 6.0+（可选，用于缓存）
- **Node.js**: 16+ 和 npm

### 检查环境
```bash
java -version
mvn -version
mysql --version
node -v
npm -v
```

## 数据库配置

### 1. 创建数据库
```bash
mysql -u root -p
```

在MySQL中执行：
```sql
CREATE DATABASE smart_health DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_health;
SOURCE /path/to/backend/src/main/resources/schema.sql;
```

或使用命令行：
```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

### 2. 验证表创建
```sql
USE smart_health;
SHOW TABLES;
```

应该看到以下10个表：
- user
- doctor_info
- patient_info
- health_record
- health_alert
- ai_suggestion
- medication
- medication_reminder
- doctor_patient_relation
- message

## 后端部署

### 1. 修改配置文件

编辑 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_health?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: YOUR_MYSQL_USERNAME
    password: YOUR_MYSQL_PASSWORD
  
  redis:
    host: localhost
    port: 6379
    password: YOUR_REDIS_PASSWORD  # 如果没有密码，留空
```

### 2. 编译项目

```bash
cd backend
mvn clean install
```

### 3. 运行后端服务

方式一：使用Maven
```bash
mvn spring-boot:run
```

方式二：使用Java命令
```bash
java -jar target/smart-health-platform-1.0.0.jar
```

### 4. 验证后端启动

访问：http://localhost:8080/api

看到以下信息表示启动成功：
```
智能健康管理平台启动成功！
API地址: http://localhost:8080/api
```

## 前端部署

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 开发环境运行

```bash
npm run dev
```

前端将在 http://localhost:3000 启动

### 3. 生产环境构建

```bash
npm run build
```

构建产物在 `frontend/dist` 目录

### 4. 使用Nginx部署（生产环境）

Nginx配置示例：
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
    
    # WebSocket代理
    location /api/ws/ {
        proxy_pass http://localhost:8080/api/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }
}
```

## Docker部署（推荐）

### 1. 创建Dockerfile（后端）

`backend/Dockerfile`:
```dockerfile
FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. 创建Dockerfile（前端）

`frontend/Dockerfile`:
```dockerfile
FROM node:16-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 3. Docker Compose

创建 `docker-compose.yml`:
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: smart_health
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./backend/src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/smart_health?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      SPRING_REDIS_HOST: redis

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql-data:
  redis-data:
```

### 4. 启动所有服务

```bash
docker-compose up -d
```

## 访问系统

### 开发环境
- 前端：http://localhost:3000
- 后端API：http://localhost:8080/api

### 生产环境
- 访问：http://your-domain.com

## 测试账号

系统预置了以下测试账号：

**患者账号1**
- 用户名：patient1
- 密码：password123
- 角色：PATIENT

**患者账号2**
- 用户名：patient2
- 密码：password123
- 角色：PATIENT

**医生账号1**
- 用户名：doctor1
- 密码：password123
- 角色：DOCTOR

**医生账号2**
- 用户名：doctor2
- 密码：password123
- 角色：DOCTOR

## API测试

### 1. 用户登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "patient1",
    "password": "password123"
  }'
```

### 2. 添加健康记录（需要token）
```bash
curl -X POST http://localhost:8080/api/health/record \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "recordDate": "2024-01-15",
    "systolicPressure": 120,
    "diastolicPressure": 80,
    "heartRate": 75,
    "weight": 70.5
  }'
```

### 3. 获取健康记录列表
```bash
curl -X GET "http://localhost:8080/api/health/records?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 常见问题

### 1. MySQL连接失败
- 检查MySQL服务是否启动
- 确认用户名和密码是否正确
- 检查数据库是否已创建

### 2. Redis连接失败
- 检查Redis服务是否启动
- 如果不需要Redis，可以注释掉相关配置

### 3. 端口被占用
- 修改application.yml中的server.port
- 或停止占用端口的进程

### 4. 前端API调用失败
- 检查后端是否启动
- 确认API代理配置是否正确

## 性能优化建议

### 数据库优化
- 为常用查询字段添加索引
- 定期清理历史数据
- 使用连接池优化连接

### 缓存优化
- 启用Redis缓存热点数据
- 设置合理的缓存过期时间
- 使用缓存预热

### 应用优化
- 启用Gzip压缩
- 配置静态资源CDN
- 使用分布式部署

## 安全建议

1. **修改JWT密钥**: 在application.yml中修改jwt.secret
2. **使用HTTPS**: 生产环境必须使用HTTPS
3. **限制API访问频率**: 添加限流中间件
4. **定期备份数据库**: 制定备份策略
5. **更新依赖版本**: 定期检查并更新依赖

## 监控与日志

### 应用日志
日志文件位置：`logs/application.log`

### 查看实时日志
```bash
tail -f logs/application.log
```

### 监控指标
- CPU使用率
- 内存使用率
- 数据库连接数
- API响应时间

## 升级维护

### 1. 备份数据
```bash
mysqldump -u root -p smart_health > backup_$(date +%Y%m%d).sql
```

### 2. 停止服务
```bash
# 停止Spring Boot
kill $(ps aux | grep 'smart-health-platform' | awk '{print $2}')

# 停止前端（如果使用pm2）
pm2 stop frontend
```

### 3. 更新代码
```bash
git pull origin main
```

### 4. 重新构建和启动
```bash
# 后端
cd backend
mvn clean install
mvn spring-boot:run &

# 前端
cd ../frontend
npm install
npm run build
```

## 技术支持

如遇到问题，请：
1. 查看日志文件
2. 检查环境配置
3. 参考系统设计文档：SYSTEM_DESIGN.md
4. 提交Issue到GitHub仓库

---

**祝您部署成功！**
