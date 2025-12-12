"""
API 路由 - Flask 接口定义
负责接收 Spring Boot 的请求并返回响应
"""
from flask import Blueprint, request, jsonify
from datetime import datetime
from threading import Thread
import logging

from ..services.task_processor import TaskProcessor
from ..utils.response import success_response, error_response

logger = logging.getLogger(__name__)

# 创建蓝图
api_bp = Blueprint('api', __name__, url_prefix='/api')


@api_bp.route('/generate', methods=['POST'])
def generate_mindmap():
    """
    接收视频处理请求（Spring Boot 调用此接口）
    
    请求参数:
    {
        "taskId": "任务ID",
        "videoUrl": "视频URL",
        "callbackUrl": "回调URL"
    }
    
    返回:
    {
        "code": 202,
        "message": "任务已接收，正在处理中",
        "data": {"taskId": "..."}
    }
    """
    try:
        data = request.get_json()
        
        # 验证参数
        if not data:
            return error_response(400, "请求体不能为空")
        
        task_id = data.get('taskId')
        video_url = data.get('videoUrl')
        callback_url = data.get('callbackUrl')
        
        if not task_id or not video_url or not callback_url:
            return error_response(400, "缺少必要参数: taskId, videoUrl, callbackUrl")
        
        logger.info(f"收到新任务: taskId={task_id}, videoUrl={video_url}")
        
        # 创建任务处理器
        processor = TaskProcessor(task_id, video_url, callback_url)
        
        # 启动异步处理线程
        thread = Thread(target=processor.process)
        thread.daemon = True
        thread.start()
        
        # 立即返回 202 Accepted
        return success_response(
            message="任务已接收，正在处理中",
            data={
                "taskId": task_id,
                "status": "processing"
            },
            status_code=202
        )
        
    except Exception as e:
        logger.error(f"处理请求时发生错误: {str(e)}")
        return error_response(500, f"服务器错误: {str(e)}")


# 健康检查路由
def register_health_routes(app):
    """注册健康检查路由"""
    
    @app.route('/')
    @app.route('/health')
    def health_check():
        """健康检查接口"""
        return jsonify({
            "service": "MindMOOC AI Service",
            "status": "running",
            "version": "1.0.0",
            "timestamp": datetime.now().isoformat()
        }), 200

