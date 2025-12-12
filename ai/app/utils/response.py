"""
响应工具 - 统一的响应格式
"""
from flask import jsonify
from typing import Any, Optional


def success_response(message: str = "操作成功", 
                     data: Optional[Any] = None, 
                     status_code: int = 200):
    """
    成功响应
    
    Args:
        message: 响应消息
        data: 响应数据
        status_code: HTTP 状态码
    """
    return jsonify({
        "code": status_code,
        "message": message,
        "data": data
    }), status_code


def error_response(code: int, message: str, data: Optional[Any] = None):
    """
    错误响应
    
    Args:
        code: 错误码
        message: 错误消息
        data: 额外数据
    """
    return jsonify({
        "code": code,
        "message": message,
        "data": data
    }), code

