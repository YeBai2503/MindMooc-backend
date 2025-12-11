package com.mindmooc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务实体类
 */
@Data
@TableName("tasks")
public class Task {
    
    /**
     * 任务ID (UUID)
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;
    
    /**
     * 视频ID
     */
    @TableField("video_id")
    private String videoId;
    
    /**
     * 任务状态 (pending, processing, completed, failed)
     */
    private String status;
    
    /**
     * 任务类型
     */
    @TableField("task_type")
    private String taskType;
    
    /**
     * 错误详情
     */
    @TableField("error_details")
    private String errorDetails;
    
    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    /**
     * 开始时间
     */
    @TableField("started_at")
    private LocalDateTime startedAt;
    
    /**
     * 完成时间
     */
    @TableField("completed_at")
    private LocalDateTime completedAt;
}

