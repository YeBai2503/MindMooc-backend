package com.mindmooc.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    // 成功
    SUCCESS(200, "请求成功"),
    
    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "没有权限访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    
    // 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    
    // 业务错误
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    INVALID_USERNAME_OR_PASSWORD(1003, "用户名或密码错误"),
    INVALID_TOKEN(1004, "Token无效或已过期"),
    
    VIDEO_NOT_FOUND(2001, "视频不存在"),
    VIDEO_UPLOAD_FAILED(2002, "视频上传失败"),
    VIDEO_ALREADY_EXISTS(2003, "视频已存在"),
    
    TASK_NOT_FOUND(3001, "任务不存在"),
    TASK_CREATE_FAILED(3002, "任务创建失败"),
    
    MINDMAP_NOT_FOUND(4001, "思维导图不存在"),
    MINDMAP_UPDATE_FAILED(4002, "思维导图更新失败"),
    NODE_NOT_FOUND(4003, "节点不存在"),
    
    FEEDBACK_CREATE_FAILED(5001, "反馈提交失败");
    
    private final Integer code;
    private final String message;
}

