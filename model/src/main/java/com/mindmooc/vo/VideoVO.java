package com.mindmooc.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频视图对象
 */
@Data
public class VideoVO {
    
    /**
     * 视频ID
     */
    private String id;
    
    /**
     * 原始文件名
     */
    private String originalFilename;
    
    /**
     * 视频标题
     */
    private String videoTitle;
    
    /**
     * 视频时长（秒）
     */
    private Integer duration;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件MIME类型
     */
    private String mimeType;
    
    /**
     * 存储URL
     */
    private String storageUrl;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

