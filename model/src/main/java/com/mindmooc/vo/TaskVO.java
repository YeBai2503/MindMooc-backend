package com.mindmooc.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务视图对象
 */
@Data
public class TaskVO {
    
    /**
     * 任务ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 视频ID
     */
    private String videoId;
    
    /**
     * 视频信息
     */
    private VideoVO video;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 任务类型
     */
    private String taskType;
    
    /**
     * 错误详情
     */
    private String errorDetails;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 开始时间
     */
    private LocalDateTime startedAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}

