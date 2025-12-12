#!/bin/bash
# MindMOOC AI Service - Linux/Mac 启动脚本

echo "========================================"
echo "   MindMOOC AI Service 启动脚本"
echo "========================================"
echo

# 检查 Python 是否安装
if ! command -v python3 &> /dev/null; then
    echo "[错误] 未检测到 Python，请先安装 Python 3.8+"
    exit 1
fi

# 检查虚拟环境是否存在
if [ ! -d "venv" ]; then
    echo "[步骤 1] 创建虚拟环境..."
    python3 -m venv venv
    echo "[完成] 虚拟环境创建成功"
    echo
fi

# 激活虚拟环境
echo "[步骤 2] 激活虚拟环境..."
source venv/bin/activate

# 安装依赖
echo "[步骤 3] 安装依赖..."
pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
echo

# 创建必要的目录
mkdir -p uploads temp logs

# 启动服务
echo "========================================"
echo "   启动 Flask 服务..."
echo "========================================"
echo
echo "服务地址: http://localhost:10020"
echo "按 Ctrl+C 停止服务"
echo

python run.py

