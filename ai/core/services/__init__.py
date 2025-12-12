"""
业务服务模块 - 核心处理逻辑

这个 __init__.py 的作用:
1. 标记 services 目录为 Python 包
2. 导出所有服务类，方便统一导入
"""

# 导出所有服务类
from .task_processor import TaskProcessor
from .video_processor import VideoProcessor
from .mindmap_generator import MindmapGenerator

# 定义公开的接口
__all__ = ['TaskProcessor', 'VideoProcessor', 'MindmapGenerator']

