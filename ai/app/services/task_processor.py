"""
任务处理器 - 统筹任务处理流程
"""
import logging
import time

from .video_processor import VideoProcessor
from .mindmap_generator import MindmapGenerator
from ..utils.callback import send_callback

logger = logging.getLogger(__name__)


class TaskProcessor:
    """任务处理器 - 协调各个处理步骤"""
    
    def __init__(self, task_id: str, video_url: str, callback_url: str):
        self.task_id = task_id
        self.video_url = video_url
        self.callback_url = callback_url
        
        # 初始化处理器
        self.video_processor = VideoProcessor()
        self.mindmap_generator = MindmapGenerator()
    
    def process(self):
        """
        主处理流程
        1. 处理视频 -> 2. 生成思维导图 -> 3. 回调 Spring Boot
        """
        try:
            logger.info(f"开始处理任务: {self.task_id}")
            
            # ==========================================
            # 步骤 1: 处理视频
            # ==========================================
            logger.info(f"步骤 1: 处理视频 - {self.video_url}")
            video_data = self.video_processor.process(self.video_url)
            
            # ==========================================
            # 步骤 2: 生成思维导图
            # ==========================================
            logger.info(f"步骤 2: 生成思维导图")
            mindmap_result = self.mindmap_generator.generate(video_data)
            
            # ==========================================
            # 步骤 3: 回调 Spring Boot
            # ==========================================
            logger.info(f"步骤 3: 回调 Spring Boot")
            callback_data = {
                "taskId": self.task_id,
                "status": "success",
                "mermaidCode": mindmap_result['mermaid_code'],
                "summary": mindmap_result['summary'],
                "originalOutput": mindmap_result['original_output']
            }
            
            success = send_callback(self.callback_url, callback_data)
            
            if success:
                logger.info(f"任务 {self.task_id} 处理成功")
            else:
                logger.error(f"任务 {self.task_id} 回调失败")
                
        except Exception as e:
            logger.error(f"处理任务 {self.task_id} 时发生错误: {str(e)}")
            
            # 发送失败回调
            error_data = {
                "taskId": self.task_id,
                "status": "failed",
                "errorMessage": str(e)
            }
            send_callback(self.callback_url, error_data)

