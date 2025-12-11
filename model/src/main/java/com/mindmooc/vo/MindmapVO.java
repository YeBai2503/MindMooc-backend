package com.mindmooc.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 思维导图视图对象
 */
@Data
public class MindmapVO {
    
    /**
     * 导图ID
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
     * 任务ID
     */
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
     * Mermaid 代码
     */
    private String mermaidCode;
    
    /**
     * 节点列表
     */
    private List<NodeVO> nodes;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

