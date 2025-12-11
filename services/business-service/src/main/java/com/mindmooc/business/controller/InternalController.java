package com.mindmooc.business.controller;

import com.mindmooc.business.service.MindmapService;
import com.mindmooc.business.service.TaskService;
import com.mindmooc.common.Result;
import com.mindmooc.dto.AiCallbackRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 内部接口控制类
 * 用于接收 AI 服务的回调等内部调用
 */
@Slf4j
@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalController {
    
    private final TaskService taskService;
    private final MindmapService mindmapService;
    
    /**
     * AI 服务回调接口
     * Flask 服务处理完成后调用此接口返回结果
     */
    @PostMapping("/tasks/callback")
    public Result<Void> handleAiCallback(@RequestBody AiCallbackRequest request) {
        log.info("收到 AI 服务回调: taskId={}, status={}", request.getTaskId(), request.getStatus());
        
        try {
            if ("success".equals(request.getStatus())) {
                // 创建思维导图
                mindmapService.createMindmapFromAiResult(request);
                
                // 更新任务状态为完成
                taskService.updateTaskStatus(request.getTaskId(), "completed", null);
            } else {
                // 更新任务状态为失败
                taskService.updateTaskStatus(request.getTaskId(), "failed", request.getErrorMessage());
            }
            
            return Result.success("回调处理成功", null);
        } catch (Exception e) {
            log.error("处理 AI 回调失败", e);
            taskService.updateTaskStatus(request.getTaskId(), "failed", "处理回调失败: " + e.getMessage());
            return Result.error("回调处理失败: " + e.getMessage());
        }
    }
}

