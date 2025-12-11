package com.mindmooc.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mindmooc.business.service.TaskService;
import com.mindmooc.common.Result;
import com.mindmooc.dto.CreateTaskRequest;
import com.mindmooc.vo.TaskVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 任务控制类
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    /**
     * 创建任务
     */
    @PostMapping
    public Result<TaskVO> createTask(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateTaskRequest request) {
        TaskVO task = taskService.createTask(userId, request);
        return Result.success("任务创建成功", task);
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public Result<TaskVO> getTaskById(@PathVariable String taskId) {
        TaskVO task = taskService.getTaskById(taskId);
        return Result.success(task);
    }
    
    /**
     * 获取用户的任务列表（分页）
     */
    @GetMapping("/my")
    public Result<IPage<TaskVO>> getUserTasks(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<TaskVO> taskPage = taskService.getUserTasks(userId, pageNum, pageSize);
        return Result.success(taskPage);
    }
}

