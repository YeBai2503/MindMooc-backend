# MindMOOC 后端

## 项目简介

MindMOOC 是一个基于 Spring Cloud 微服务架构的慕课视频思维导图生成系统。用户上传视频文件后，系统通过 AI 服务分析视频内容并生成思维导图（Mermaid 格式），同时支持用户自定义修改思维导图节点。

## 技术栈

- **Spring Boot 3.3.4**
- **Spring Cloud 2023.0.3**
- **Spring Cloud Alibaba 2023.0.3.2**
- **MyBatis Plus 3.5.5**
- **MySQL 8.0**
- **JWT (JSON Web Token)**
- **Nacos** - 服务注册与发现
- **Spring Cloud Gateway** - API 网关

## 项目结构

```
backend/
├── gateway/                    # API 网关
│   ├── src/main/java/
│   │   └── com/mindmooc/gateway/
│   │       ├── GatewayMainApplication.java
│   │       ├── config/         # JWT、认证配置
│   │       └── filter/         # 全局过滤器（JWT认证）
│   └── src/main/resources/
│       ├── application.yml
│       └── application-route.yml
│
├── services/
│   └── business-service/       # 业务服务
│       ├── src/main/java/
│       │   └── com/mindmooc/business/
│       │       ├── BusinessMainApplication.java
│       │       ├── config/     # 配置类
│       │       ├── controller/ # 控制器层
│       │       ├── service/    # 服务层
│       │       ├── mapper/     # MyBatis Mapper
│       │       ├── client/     # Feign 客户端
│       │       ├── utils/      # 工具类
│       │       └── common/     # 通用类
│       └── src/main/resources/
│           └── application.yml
│
├── model/                      # 共享模型模块
│   └── src/main/java/
│       └── com/mindmooc/model/
│           ├── entity/         # 实体类
│           ├── dto/            # 数据传输对象
│           ├── vo/             # 视图对象
│           └── common/         # 通用类（Result、异常等）
│
├── database/                   # 数据库脚本
│   └── init.sql
│
├── pom.xml                     # 父 POM
└── README-BACKEND.md           # 本文件
```

## 核心功能模块

### 1. 用户模块

- 用户注册
- 用户登录（JWT Token）
- 获取用户信息
- 更新用户信息

### 2. 视频模块

- 视频上传（支持文件去重）
- 视频查询
- 视频列表（分页）
- 视频删除

### 3. 任务模块

- 创建处理任务
- 查询任务状态
- 用户任务列表（分页）
- AI 服务回调接口

### 4. 思维导图模块

- 查询思维导图
- 获取导图节点（树形结构）
- 添加节点
- 更新节点
- 删除节点（级联删除子节点）
- 重新生成 Mermaid 代码

### 5. 反馈模块

- 提交反馈（支持匿名）
- 查询反馈
- 反馈列表（分页）
- 更新反馈状态（管理员）

## API 接口

### 用户接口

- `POST /api/users/register` - 用户注册
- `POST /api/users/login` - 用户登录
- `GET /api/users/me` - 获取当前用户信息
- `PUT /api/users/me` - 更新用户信息

### 视频接口

- `POST /api/videos/upload` - 上传视频
- `GET /api/videos/{videoId}` - 获取视频信息
- `GET /api/videos/list` - 获取视频列表
- `DELETE /api/videos/{videoId}` - 删除视频

### 任务接口

- `POST /api/tasks` - 创建任务
- `GET /api/tasks/{taskId}` - 获取任务详情
- `GET /api/tasks/my` - 获取我的任务列表

### 思维导图接口

- `GET /api/mindmaps/{mindmapId}` - 获取思维导图
- `GET /api/mindmaps/task/{taskId}` - 根据任务ID获取思维导图
- `GET /api/mindmaps/my` - 获取我的思维导图列表
- `GET /api/mindmaps/{mindmapId}/nodes` - 获取节点列表
- `POST /api/mindmaps/{mindmapId}/nodes` - 添加节点
- `PUT /api/mindmaps/nodes/{nodeId}` - 更新节点
- `DELETE /api/mindmaps/nodes/{nodeId}` - 删除节点
- `POST /api/mindmaps/{mindmapId}/regenerate` - 重新生成 Mermaid 代码

### 反馈接口

- `POST /api/feedback` - 提交反馈
- `GET /api/feedback/{feedbackId}` - 获取反馈详情
- `GET /api/feedback/my` - 获取我的反馈列表
- `GET /api/feedback/all` - 获取所有反馈（管理员）
- `PUT /api/feedback/{feedbackId}/status` - 更新反馈状态（管理员）

### 内部接口

- `POST /api/internal/tasks/callback` - AI 服务回调接口

## 启动步骤

### 1. 环境准备

确保已安装以下软件：

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Nacos 2.x

### 2. 启动 Nacos

```bash
cd nacos/bin
# Windows
.\startup.cmd -m standalone

# Linux/Mac
sh startup.sh -m standalone
```

Nacos 默认端口：8848
访问：http://localhost:8848/nacos
默认用户名/密码：nacos/nacos

### 3. 创建数据库

```bash
# 连接 MySQL
mysql -u root -p

# 执行初始化脚本
source database/init.sql
```

或者使用数据库管理工具（如 Navicat）导入 `database/init.sql` 文件。

### 4. 修改配置文件

修改 `services/business-service/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mindmooc?...
    username: root
    password: 你的密码
```

修改文件上传路径（可选）：

```yaml
file:
  upload:
    path: 你的文件存储路径
```

### 5. 验证服务

- Gateway: http://localhost:80
- Business Service: http://localhost:10010
- Nacos: http://localhost:8848/nacos

测试登录接口：

```bash
curl -X POST http://localhost/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

## 认证机制

### JWT Token 流程

1. **用户登录**：前端发送用户名和密码到 `/api/users/login`
2. **生成 Token**：业务服务验证成功后生成 JWT Token 返回
3. **携带 Token**：前端在后续请求的 Header 中携带 Token：
   
   ```
   Authorization: Bearer <your_token>
   ```
4. **网关认证**：Gateway 拦截请求，验证 Token
5. **转发请求**：验证通过后，Gateway 将用户信息（userId、username）添加到请求头并转发给业务服务

### 不需要认证的接口

以下接口不需要 Token：

- `/api/users/register` - 注册
- `/api/users/login` - 登录
- `/api/feedback` - 提交反馈（支持匿名）
- `/api/internal/**` - 内部接口

## 数据库设计

### 主要表结构

1. **users** - 用户表
2. **videos** - 视频表
3. **tasks** - 任务表
4. **mindmaps** - 思维导图表
5. **mindmap_nodes** - 思维导图节点表
6. **feedback** - 反馈表

详细的表结构和字段说明请参考 `database/init.sql` 文件。

## 与 AI 服务集成

### Flask AI 服务接口规范

**请求接口**：`POST /api/generate`

**请求参数**：

```json
{
  "taskId": "任务ID",
  "videoUrl": "视频URL",
  "callbackUrl": "回调URL（http://localhost:10010/api/internal/tasks/callback）"
}
```

**回调参数**：

```json
{
  "taskId": "任务ID",
  "status": "success|failed",
  "mermaidCode": "生成的Mermaid代码",
  "summary": "视频概要",
  "errorMessage": "错误信息（失败时）",
  "originalOutput": "AI原始输出（JSON格式）"
}
```

Flask 服务处理完成后，应调用回调 URL 返回结果。

## 开发建议

### 添加新的接口

1. 在 `model` 模块创建 DTO 和 VO
2. 在 `business-service` 的 Service 层实现业务逻辑
3. 在 Controller 层创建接口
4. 如果需要认证，确保路径不在 `auth.skip-paths` 中

### 调试技巧

1. 查看 MyBatis Plus SQL 日志（已配置）
2. 查看 Nacos 服务列表，确认服务已注册
3. 使用 Postman 测试接口
4. 查看 Gateway 日志，确认路由和认证是否正常

## 常见问题

### 1. 服务无法注册到 Nacos

- 检查 Nacos 是否启动
- 检查 `application.yml` 中的 Nacos 地址是否正确
- 检查网络连接

### 2. 数据库连接失败

- 检查 MySQL 是否启动
- 检查数据库用户名和密码
- 检查数据库是否已创建

### 3. Token 验证失败

- 检查 JWT 密钥是否一致（Gateway 和 Business Service）
- 检查 Token 是否过期
- 检查请求头格式：`Authorization: Bearer <token>`

### 4. 文件上传失败

- 检查上传目录是否存在且有写权限
- 检查文件大小是否超过限制（默认 500MB）

## 后续计划

- [ ] 接入真实的 Flask AI 服务
- [ ] 实现文件存储到云存储（如阿里云 OSS、AWS S3）
- [ ] 添加视频时长解析功能
- [ ] 优化 Mermaid 代码生成算法
- [ ] 添加用户权限管理
- [ ] 实现批量任务处理
- [ ] 添加任务队列（RabbitMQ/Kafka）
- [ ] 实现实时任务进度推送（WebSocket）

# 


