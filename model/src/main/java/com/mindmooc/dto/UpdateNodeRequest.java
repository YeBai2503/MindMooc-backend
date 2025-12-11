package com.mindmooc.dto;

import lombok.Data;

/**
 * 更新节点请求 DTO
 */
@Data
public class UpdateNodeRequest {
    
    /**
     * 节点ID
     */
    private String id;
    
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
}

