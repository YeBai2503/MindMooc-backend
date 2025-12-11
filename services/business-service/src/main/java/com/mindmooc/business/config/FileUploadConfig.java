package com.mindmooc.business.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * 文件上传配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    
    /**
     * 文件上传路径
     */
    private String path;
    
    /**
     * 文件访问基础URL
     */
    private String baseUrl;
    
    /**
     * 初始化上传目录
     */
    @PostConstruct
    public void init() {
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
}

