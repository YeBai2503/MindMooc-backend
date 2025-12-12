@echo off
REM MindMOOC AI Service - Windows 启动脚本

echo ========================================
echo    MindMOOC AI Service 启动脚本
echo ========================================
echo.

REM 检查 Python 是否安装
python --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Python，请先安装 Python 3.8+
    pause
    exit /b 1
)

REM 检查虚拟环境是否存在
if not exist "venv" (
    echo [步骤 1] 创建虚拟环境...
    python -m venv venv
    echo [完成] 虚拟环境创建成功
    echo.
)

REM 激活虚拟环境
echo [步骤 2] 激活虚拟环境...
call venv\Scripts\activate.bat

REM 安装依赖
echo [步骤 3] 安装依赖...
pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
echo.

REM 创建必要的目录
if not exist "uploads" mkdir uploads
if not exist "temp" mkdir temp
if not exist "logs" mkdir logs

REM 启动服务
echo ========================================
echo    启动 Flask 服务...
echo ========================================
echo.
echo 服务地址: http://localhost:10020
echo 按 Ctrl+C 停止服务
echo.

python app.py

pause

