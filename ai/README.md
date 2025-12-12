# MindMOOC AI Service

基于 Flask 的 AI 视频分析服务，用于生成思维导图。

## 🎯 功能特性

- ✅ 接收 Spring Boot 发送的视频处理请求
- ✅ 异步处理视频（可集成大模型）
- ✅ 生成 Mermaid 格式的思维导图代码
- ✅ 自动回调 Spring Boot 返回处理结果
- ✅ 模块化架构，易于扩展

## 📁 项目结构

```
ai/
├── core/                          # 核心代码目录
│   ├── api/                       # API 接口层（与 Spring Boot 对接）
│   ├── services/                  # 业务服务层（核心处理逻辑）
│   │   ├── task_processor.py     # 任务处理器 ✅
│   │   ├── video_processor.py    # 视频处理 ⚠️ TODO
│   │   └── mindmap_generator.py  # 思维导图生成 ⚠️ TODO
│   └── utils/                     # 工具模块
│
├── app.py                         # Flask 主应用入口
├── requirements.txt               # Python 依赖
├── start.bat / start.sh          # 一键启动脚本
│
├── tests/                         # 测试脚本
│   └── test_api.py               # API 测试
│
├── uploads/                       # 文件上传目录
├── temp/                          # 临时文件目录
└── logs/                          # 日志目录
```

## 🚀 快速启动

### 方法 1: 使用启动脚本（推荐）

```bash
# Windows
start.bat

# Linux/Mac
chmod +x start.sh
./start.sh
```

### 方法 2: 手动启动

```bash
# 1. 创建虚拟环境
python -m venv venv

# 2. 激活虚拟环境
# Windows:
venv\Scripts\activate
# Linux/Mac:
source venv/bin/activate

# 3. 安装依赖
pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple

# 4. 启动服务
python app.py
```

服务将在 `http://localhost:10020` 启动。

## ✅ 验证服务

```bash
# 健康检查
curl http://localhost:10020/health

# 或在浏览器打开
http://localhost:10020
```

## 🧪 运行测试

```bash
cd tests
python test_api.py
```

## 🔧 与 Spring Boot 集成

### API 接口

#### 1. 生成思维导图

**POST** `/api/generate`

**请求参数**:

```json
{
  "taskId": "任务ID",
  "videoUrl": "视频URL",
  "callbackUrl": "回调URL"
}
```

**响应** (202 Accepted):

```json
{
  "code": 202,
  "message": "任务已接收，正在处理中",
  "data": {
    "taskId": "xxx",
    "status": "processing"
  }
}
```

#### 2. 健康检查

**GET** `/health`

**响应**:

```json
{
  "service": "MindMOOC AI Service",
  "status": "running",
  "version": "1.0.0"
}
```

## ⚠️ TODO - 需要实现的功能

### 优先级 1: 视频处理模块

编辑 `core/services/video_processor.py`:

- [ ] 实现视频下载功能
- [ ] 实现音频提取功能
- [ ] 集成语音识别 API
- [ ] 实现字幕提取功能

推荐工具:

- `moviepy` - 视频/音频处理
- `whisper` - OpenAI 语音识别
- `opencv-python` - 视频帧处理

### 优先级 2: 思维导图生成模块

编辑 `core/services/mindmap_generator.py`:

- [ ] 集成大模型 API（OpenAI/Claude/通义千问等）
- [ ] 实现提示词构建
- [ ] 实现 Mermaid 代码生成
- [ ] 实现视频概要生成

推荐模型:

- OpenAI GPT-4
- Anthropic Claude
- 阿里云通义千问

## 🐛 调试

### 查看日志

```bash
# 实时查看
tail -f logs/ai-service.log

# 查看错误
grep ERROR logs/ai-service.log
```

### 单独测试模块

```python
# 测试视频处理
from core.services.video_processor import VideoProcessor
processor = VideoProcessor()
result = processor.process("test_video_url")
print(result)

# 测试思维导图生成
from core.services.mindmap_generator import MindmapGenerator
generator = MindmapGenerator()
mindmap = generator.generate(video_data)
print(mindmap)
```

## 🚢 生产部署

### 使用 Gunicorn

```bash
pip install gunicorn
gunicorn -w 4 -b 0.0.0.0:10020 --timeout 300 app:app
```

### 使用 Docker

```bash
# 构建镜像
cd ai
docker build -t mindmooc-ai:latest .

# 运行容器
docker run -d -p 10020:10020 \
  --name mindmooc-ai \
  -e SPRING_BOOT_URL=http://host.docker.internal:10010 \
  mindmooc-ai:latest
```

## ✅ 已完成的功能

1. ✅ **模块化架构** - 清晰的代码结构
2. ✅ **API 接口** - 与 Spring Boot 对接完成
3. ✅ **任务处理流程** - 统筹各个步骤
4. ✅ **回调机制** - 自动回调 Spring Boot
5. ✅ **日志系统** - 完整的日志记录
6. ✅ **错误处理** - 异常捕获和处理
7. ✅ **示例数据** - 可直接测试对接

## 💡 开发建议

1. **从小处开始**: 先让一个简单视频能跑通全流程
2. **逐步优化**: 再添加更复杂的处理逻辑
3. **记录日志**: 多用 `logger.info()` 记录关键步骤
4. **错误处理**: 用 `try-except` 捕获异常
5. **测试驱动**: 写一个测试视频，反复测试

## 🎯 里程碑

* [ ] **里程碑 1**: 能下载并识别视频文字
* [ ] **里程碑 2**: 能调用大模型生成基本结构
* [ ] **里程碑 3**: 能生成正确的 Mermaid 代码
* [ ] **里程碑 4**: 思维导图结构合理、层次清晰
* [ ] **里程碑 5**: 性能优化、支持长视频

## 📚 参考资料

### 视频处理

* [MoviePy 文档](https://zulko.github.io/moviepy/)
* [OpenCV Python 教程](https://docs.opencv.org/4.x/d6/d00/tutorial_py_root.html)
* [Whisper AI 语音识别](https://github.com/openai/whisper)

### 大模型调用

* [OpenAI API 文档](https://platform.openai.com/docs)
* [Anthropic Claude API](https://docs.anthropic.com/)
* [通义千问 API](https://help.aliyun.com/document_detail/2712195.html)

### Mermaid 语法

* [Mermaid 官方文档](https://mermaid.js.org/)
* [思维导图语法](https://mermaid.js.org/syntax/mindmap.html)
