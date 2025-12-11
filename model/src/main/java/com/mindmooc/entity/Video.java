package com.mindmooc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频实体类
 */
@Data
@TableName("videos")
public class Video {
    
    /**
     * 视频ID (UUID)
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 文件哈希值（用于去重）
     */
    @TableField("file_hash")
    private String fileHash;
    
    /**
     * 存储路径/URL
     */
    @TableField("storage_url")
    private String storageUrl;
    
    /**
     * 原始文件名
     */
    @TableField("original_filename")
    private String originalFilename;
    
    /**
     * 视频标题
     */
    @TableField("video_title")
    private String videoTitle;
    
    /**
     * 视频时长（秒）
     */
    private Integer duration;
    
    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;
    
    /**
     * 文件MIME类型
     */
    @TableField("mime_type")
    private String mimeType;
    
    /**
     * AI原始输出（JSON格式）
     */
    @TableField("original_ai_output")
    private String originalAiOutput;
    
    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}

