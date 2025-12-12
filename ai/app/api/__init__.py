"""
API 接口模块 - 负责与 Spring Boot 对接

这个 __init__.py 的作用:
1. 标记 api 目录为 Python 包
2. 导出 Flask 蓝图，方便在 run.py 中导入
"""

# 导出 API 蓝图和路由注册函数
from .routes import api_bp, register_health_routes

# 定义公开的接口
__all__ = ['api_bp', 'register_health_routes']

