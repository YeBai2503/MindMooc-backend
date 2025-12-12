"""
MindMOOC AI Service - Flask 主应用入口
"""
from flask import Flask
import logging

# 现在可以使用简化的导入路径
from app.api import api_bp, register_health_routes
from app.utils import setup_logger

# 创建 Flask 应用
app = Flask(__name__)

# 配置
app.config['JSON_AS_ASCII'] = False  # 支持中文
app.config['MAX_CONTENT_LENGTH'] = 500 * 1024 * 1024  # 最大 500MB

# 设置日志
setup_logger(app)

logger = logging.getLogger(__name__)

# 注册路由
register_health_routes(app)  # 健康检查路由
app.register_blueprint(api_bp)  # API 路由


@app.errorhandler(404)
def not_found(error):
    """404 错误处理"""
    return {
        "code": 404,
        "message": "接口不存在",
        "data": None
    }, 404


@app.errorhandler(500)
def internal_error(error):
    """500 错误处理"""
    logger.error(f"Internal error: {str(error)}")
    return {
        "code": 500,
        "message": "服务器内部错误",
        "data": None
    }, 500


if __name__ == '__main__':
    logger.info("=" * 50)
    logger.info("MindMOOC AI Service 启动中...")
    logger.info("=" * 50)
    
    app.run(
        host='0.0.0.0',
        port=10020,
        debug=True,
        threaded=True
    )
