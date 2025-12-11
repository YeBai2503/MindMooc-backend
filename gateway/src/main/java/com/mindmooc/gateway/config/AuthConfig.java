package com.mindmooc.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 认证配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {
    
    /**
     * 不需要认证的路径列表
     */
    private List<String> skipPaths;
}

