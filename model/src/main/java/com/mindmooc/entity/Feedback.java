package com.mindmooc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈实体类
 */
@Data
@TableName("feedback")
public class Feedback {
    
    /**
     * 反馈ID (UUID)
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID（允许为空，支持匿名反馈）
     */
    @TableField("user_id")
    private String userId;
    
    /**
     * 反馈类型 (bug, suggestion, other)
     */
    private String type;
    
    /**
     * 反馈详情
     */
    private String details;
    
    /**
     * 反馈状态 (open, closed)
     */
    private String status;
    
    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}

