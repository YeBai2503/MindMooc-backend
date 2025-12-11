package com.mindmooc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建反馈请求 DTO
 */
@Data
public class CreateFeedbackRequest {
    
    /**
     * 反馈类型 (bug, suggestion, other)
     */
    @NotBlank(message = "反馈类型不能为空")
    private String type;
    
    /**
     * 反馈详情
     */
    @NotBlank(message = "反馈内容不能为空")
    private String details;
}

