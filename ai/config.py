"""
配置文件
"""
import os
from dotenv import load_dotenv

# 加载 .env 文件
load_dotenv()


class Config:
    """基础配置"""
    
    # Flask 配置
    SECRET_KEY = os.getenv('SECRET_KEY', 'mindmooc-ai-secret-key-2024')
    DEBUG = os.getenv('DEBUG', 'True').lower() == 'true'
    
    # 服务配置
    HOST = os.getenv('HOST', '0.0.0.0')
    PORT = int(os.getenv('PORT', 10020))
    
    # 文件上传配置
    MAX_CONTENT_LENGTH = 500 * 1024 * 1024  # 500MB
    UPLOAD_FOLDER = os.getenv('UPLOAD_FOLDER', './uploads')
    
    # Spring Boot 服务地址
    SPRING_BOOT_URL = os.getenv('SPRING_BOOT_URL', 'http://localhost:10010')
    
    # 视频处理配置
    VIDEO_TEMP_DIR = os.getenv('VIDEO_TEMP_DIR', './temp')
    VIDEO_ALLOWED_EXTENSIONS = {'mp4', 'avi', 'mov', 'mkv', 'flv', 'wmv'}
    
    # AI 模型配置（示例）
    OPENAI_API_KEY = os.getenv('OPENAI_API_KEY', '')
    MODEL_NAME = os.getenv('MODEL_NAME', 'gpt-4')
    
    # 日志配置
    LOG_LEVEL = os.getenv('LOG_LEVEL', 'INFO')
    LOG_FILE = os.getenv('LOG_FILE', './logs/ai-service.log')


class DevelopmentConfig(Config):
    """开发环境配置"""
    DEBUG = True


class ProductionConfig(Config):
    """生产环境配置"""
    DEBUG = False


# 配置映射
config = {
    'development': DevelopmentConfig,
    'production': ProductionConfig,
    'default': DevelopmentConfig
}


def get_config(env='default'):
    """获取配置"""
    return config.get(env, config['default'])

