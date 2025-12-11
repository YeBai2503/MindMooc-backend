package com.mindmooc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 思维导图实体类
 */
@Data
@TableName("mindmaps")
public class Mindmap {
    
    /**
     * 导图ID (UUID)
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 用户ID（所有者）
     */
    @TableField("user_id")
    private String userId;
    
    /**
     * 视频ID
     */
    @TableField("video_id")
    private String videoId;
    
    /**
     * 任务ID
     */
    @TableField("task_id")
    private String taskId;
    
    /**
     * 导图标题
     */
    private String title;
    
    /**
     * 视频概要
     */
    private String summary;
    
    /**
     * 缓存的Mermaid代码
     */
    @TableField("mermaid_cache")
    private String mermaidCache;
    
    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}

