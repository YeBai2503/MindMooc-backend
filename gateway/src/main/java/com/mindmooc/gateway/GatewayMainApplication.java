package com.mindmooc.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Gateway 网关启动类
 * 
 * 功能：
 * 1. API 网关 - 所有请求的统一入口
 * 2. JWT 认证 - 在 AuthGlobalFilter 中实现
 * 3. 服务路由 - 转发请求到 business-service
 * 4. CORS 处理 - 跨域配置
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayMainApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayMainApplication.class, args);
    }
}

