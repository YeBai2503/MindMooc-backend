package com.mindmooc.dto;

import lombok.Data;

/**
 * AI 服务回调请求 DTO
 */
@Data
public class AiCallbackRequest {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 处理状态 (success, failed)
     */
    private String status;
    
    /**
     * 生成的 Mermaid 代码
     */
    private String mermaidCode;
    
    /**
     * 视频概要
     */
    private String summary;
    
    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
    
    /**
     * AI 原始输出（JSON格式的节点结构）
     */
    private String originalOutput;
}

