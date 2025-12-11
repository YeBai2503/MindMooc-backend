package com.mindmooc.business.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * AI 服务 Feign 客户端
 * 用于调用 Flask AI 服务
 */
@FeignClient(name = "ai-service", url = "${ai.service.url}")
public interface AiServiceClient {
    
    /**
     * 调用 AI 服务生成思维导图
     * @param request 包含 taskId, videoUrl, callbackUrl
     * @return AI 服务响应
     */
    @PostMapping("/api/generate")
    Map<String, Object> generateMindmap(@RequestBody Map<String, String> request);
}

