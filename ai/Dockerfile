# MindMOOC AI Service - Docker 镜像

FROM python:3.11-slim

# 设置工作目录
WORKDIR /app

# 安装系统依赖（如果需要视频处理）
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libsm6 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*

# 复制依赖文件
COPY requirements.txt .

# 安装 Python 依赖
RUN pip install --no-cache-dir -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple

# 复制应用代码
COPY . .

# 创建必要的目录
RUN mkdir -p uploads temp logs

# 暴露端口
EXPOSE 10020

# 设置环境变量
ENV FLASK_APP=run.py
ENV PYTHONUNBUFFERED=1

# 启动命令（生产环境使用 Gunicorn）
CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:10020", "--timeout", "300", "run:app"]
