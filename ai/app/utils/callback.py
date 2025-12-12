"""
回调工具 - 负责回调 Spring Boot
"""
import requests
import logging
from typing import Dict, Any

logger = logging.getLogger(__name__)


def send_callback(callback_url: str, data: Dict[str, Any], timeout: int = 30) -> bool:
    """
    发送回调到 Spring Boot
    
    Args:
        callback_url: Spring Boot 的回调接口地址
        data: 回调数据
        timeout: 超时时间（秒）
        
    Returns:
        是否成功
    """
    try:
        logger.info(f"发送回调到: {callback_url}")
        logger.debug(f"回调数据: {data}")
        
        response = requests.post(
            callback_url,
            json=data,
            headers={'Content-Type': 'application/json'},
            timeout=timeout
        )
        
        if response.status_code == 200:
            logger.info(f"回调成功: {response.status_code}")
            return True
        else:
            logger.error(f"回调失败: {response.status_code} - {response.text}")
            return False
            
    except requests.exceptions.Timeout:
        logger.error(f"回调超时: {callback_url}")
        return False
        
    except requests.exceptions.ConnectionError as e:
        logger.error(f"回调连接失败: {str(e)}")
        return False
        
    except Exception as e:
        logger.error(f"回调发生错误: {str(e)}")
        return False

