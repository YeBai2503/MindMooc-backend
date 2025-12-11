package com.mindmooc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 思维导图节点实体类
 */
@Data
@TableName("mindmap_nodes")
public class MindmapNode {
    
    /**
     * 节点ID (UUID)
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 导图ID
     */
    @TableField("map_id")
    private String mapId;
    
    /**
     * 父节点ID
     */
    @TableField("parent_id")
    private String parentId;
    
    /**
     * 节点内容
     */
    private String content;
    
    /**
     * 视频起始时间（秒）
     */
    @TableField("start_time")
    private Integer startTime;
    
    /**
     * 视频结束时间（秒）
     */
    @TableField("end_time")
    private Integer endTime;
    
    /**
     * 同级节点的顺序
     */
    @TableField("node_order")
    private Integer nodeOrder;
    
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

