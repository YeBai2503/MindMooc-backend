package com.mindmooc.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 思维导图节点视图对象
 */
@Data
public class NodeVO {
    
    /**
     * 节点ID
     */
    private String id;
    
    /**
     * 导图ID
     */
    private String mapId;
    
    /**
     * 父节点ID
     */
    private String parentId;
    
    /**
     * 节点内容
     */
    private String content;
    
    /**
     * 起始时间（秒）
     */
    private Integer startTime;
    
    /**
     * 结束时间（秒）
     */
    private Integer endTime;
    
    /**
     * 节点顺序
     */
    private Integer nodeOrder;
    
    /**
     * 子节点列表
     */
    private List<NodeVO> children;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

