package com.mindmooc.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈视图对象
 */
@Data
public class FeedbackVO {
    
    /**
     * 反馈ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 反馈类型
     */
    private String type;
    
    /**
     * 反馈详情
     */
    private String details;
    
    /**
     * 反馈状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

