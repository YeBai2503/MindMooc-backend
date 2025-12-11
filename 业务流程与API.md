# MindMOOC 业务流程

## 📋 API 列表

### 🔐 用户模块（不需要认证的接口）

- `POST /api/users/register` - 用户注册
- `POST /api/users/login` - 用户登录

### 👤 用户模块（需要认证）

- `GET /api/users/me` - 获取当前用户信息
- `PUT /api/users/me` - 更新用户信息
- `GET /api/users/{userId}` - 获取指定用户信息

### 🎬 视频模块

- `POST /api/videos/upload` - 上传视频（multipart/form-data）
- `GET /api/videos/{videoId}` - 获取视频信息
- `GET /api/videos/list?pageNum=1&pageSize=10` - 获取视频列表
- `DELETE /api/videos/{videoId}` - 删除视频

### 📝 任务模块

- `POST /api/tasks` - 创建任务
- `GET /api/tasks/{taskId}` - 获取任务详情
- `GET /api/tasks/my?pageNum=1&pageSize=10` - 获取我的任务列表

### 🗺️ 思维导图模块

- `GET /api/mindmaps/{mindmapId}` - 获取思维导图
- `GET /api/mindmaps/task/{taskId}` - 根据任务ID获取思维导图
- `GET /api/mindmaps/my?pageNum=1&pageSize=10` - 获取我的思维导图列表
- `GET /api/mindmaps/{mindmapId}/nodes` - 获取节点列表（树形结构）
- `POST /api/mindmaps/{mindmapId}/nodes` - 添加节点
- `PUT /api/mindmaps/nodes/{nodeId}` - 更新节点
- `DELETE /api/mindmaps/nodes/{nodeId}` - 删除节点
- `POST /api/mindmaps/{mindmapId}/regenerate` - 重新生成 Mermaid 代码

### 💬 反馈模块

- `POST /api/feedback` - 提交反馈（支持匿名）
- `GET /api/feedback/{feedbackId}` - 获取反馈详情
- `GET /api/feedback/my?pageNum=1&pageSize=10` - 获取我的反馈列表
- `GET /api/feedback/all?pageNum=1&pageSize=10&status=open` - 获取所有反馈（管理员）
- `PUT /api/feedback/{feedbackId}/status?status=closed` - 更新反馈状态（管理员）

### 🔧 内部接口（由 Flask AI 服务调用）

- `POST /api/internal/tasks/callback` - AI 服务回调接口

## 🎯 完整业务流程示例

### 场景：用户上传视频并生成思维导图

#### 步骤 1：用户注册/登录

```bash
# 登录
curl -X POST http://localhost/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'

# 保存返回的 token
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

#### 步骤 2：上传视频

```bash
curl -X POST http://localhost/api/videos/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/video.mp4" \
  -F "videoTitle=我的慕课视频"

# 保存返回的 videoId
VIDEO_ID="video-id-xxx"
```

#### 步骤 3：创建任务

```bash
curl -X POST http://localhost/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"videoId\":\"$VIDEO_ID\",\"taskType\":\"common\"}"

# 保存返回的 taskId
TASK_ID="task-id-xxx"
```

#### 步骤 4：查询任务状态

```bash
curl -X GET http://localhost/api/tasks/$TASK_ID \
  -H "Authorization: Bearer $TOKEN"

# 任务状态：pending -> processing -> completed
```

#### 步骤 5：获取思维导图

```bash
# 任务完成后，根据任务ID获取思维导图
curl -X GET http://localhost/api/mindmaps/task/$TASK_ID \
  -H "Authorization: Bearer $TOKEN"

# 保存返回的 mindmapId
MINDMAP_ID="mindmap-id-xxx"
```

#### 步骤 6：修改思维导图节点

```bash
# 添加节点
curl -X POST http://localhost/api/mindmaps/$MINDMAP_ID/nodes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"parentId":"parent-node-id","content":"新节点内容","startTime":10,"endTime":20,"nodeOrder":1}'

# 更新节点
curl -X PUT http://localhost/api/mindmaps/nodes/node-id-xxx \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"更新后的节点内容"}'

# 删除节点
curl -X DELETE http://localhost/api/mindmaps/nodes/node-id-xxx \
  -H "Authorization: Bearer $TOKEN"
```

#### 步骤 7：重新生成 Mermaid 代码

```bash
curl -X POST http://localhost/api/mindmaps/$MINDMAP_ID/regenerate \
  -H "Authorization: Bearer $TOKEN"
```

## ❓ 常见问题

### Q1: 服务启动失败？

**A:** 检查以下几点：

1. Nacos 是否已启动（端口 8848）
2. MySQL 是否已启动
3. 数据库是否已初始化
4. 配置文件中的数据库密码是否正确

### Q2: Token 验证失败？

**A:** 确保：

1. 请求头格式正确：`Authorization: Bearer <token>`
2. Token 未过期（默认 24 小时）
3. JWT 密钥配置一致

### Q3: 文件上传失败？

**A:** 检查：

1. 上传目录是否存在：`G:/work/GraduationProject/code/MindMooc/backend/uploads/`
2. 目录是否有写权限
3. 文件大小是否超过 500MB

### Q4: 数据库连接失败？

**A:** 检查：

1. MySQL 是否启动
2. 数据库 `mindmooc` 是否已创建
3. 用户名密码是否正确


