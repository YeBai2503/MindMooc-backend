package com.mindmooc.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindmooc.business.client.AiServiceClient;
import com.mindmooc.business.mapper.TaskMapper;
import com.mindmooc.business.mapper.VideoMapper;
import com.mindmooc.business.utils.BeanCopyUtil;
import com.mindmooc.common.BusinessException;
import com.mindmooc.common.ResultCode;
import com.mindmooc.dto.CreateTaskRequest;
import com.mindmooc.entity.Task;
import com.mindmooc.entity.Video;
import com.mindmooc.vo.TaskVO;
import com.mindmooc.vo.VideoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskMapper taskMapper;
    private final VideoMapper videoMapper;
    private final AiServiceClient aiServiceClient;
    
    @Value("${server.port}")
    private String serverPort;
    
    /**
     * 创建任务
     */
    @Transactional(rollbackFor = Exception.class)
    public TaskVO createTask(String userId, CreateTaskRequest request) {
        // 验证视频是否存在
        Video video = videoMapper.selectById(request.getVideoId());
        if (video == null) {
            throw new BusinessException(ResultCode.VIDEO_NOT_FOUND);
        }
        
        // 创建任务记录
        Task task = new Task();
        task.setUserId(userId);
        task.setVideoId(request.getVideoId());
        task.setStatus("pending");
        task.setTaskType(request.getTaskType());
        task.setCreatedAt(LocalDateTime.now());
        
        taskMapper.insert(task);
        
        // 异步调用 AI 服务
        try {
            Map<String, String> aiRequest = new HashMap<>();
            aiRequest.put("taskId", task.getId());
            aiRequest.put("videoUrl", video.getStorageUrl());
            aiRequest.put("callbackUrl", "http://localhost:" + serverPort + "/api/internal/tasks/callback");
            
            // 调用 Flask AI 服务（异步处理）
            aiServiceClient.generateMindmap(aiRequest);
            
            // 更新任务状态为 processing
            task.setStatus("processing");
            task.setStartedAt(LocalDateTime.now());
            taskMapper.updateById(task);
            
        } catch (Exception e) {
            log.error("调用 AI 服务失败", e);
            task.setStatus("failed");
            task.setErrorDetails("调用 AI 服务失败: " + e.getMessage());
            taskMapper.updateById(task);
        }
        
        log.info("任务创建成功: {}", task.getId());
        
        // 构建返回 VO
        TaskVO taskVO = BeanCopyUtil.copy(task, TaskVO.class);
        taskVO.setVideo(BeanCopyUtil.copy(video, VideoVO.class));
        
        return taskVO;
    }
    
    /**
     * 根据ID获取任务信息
     */
    public TaskVO getTaskById(String taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        
        TaskVO taskVO = BeanCopyUtil.copy(task, TaskVO.class);
        
        // 加载视频信息
        Video video = videoMapper.selectById(task.getVideoId());
        if (video != null) {
            taskVO.setVideo(BeanCopyUtil.copy(video, VideoVO.class));
        }
        
        return taskVO;
    }
    
    /**
     * 获取用户的任务列表（分页）
     */
    public IPage<TaskVO> getUserTasks(String userId, int pageNum, int pageSize) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUserId, userId)
               .orderByDesc(Task::getCreatedAt);
        
        IPage<Task> taskPage = taskMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<TaskVO> voPage = new Page<>(taskPage.getCurrent(), taskPage.getSize(), taskPage.getTotal());
        List<TaskVO> taskVOList = BeanCopyUtil.copyList(taskPage.getRecords(), TaskVO.class);
        
        // 加载视频信息
        for (int i = 0; i < taskVOList.size(); i++) {
            Task task = taskPage.getRecords().get(i);
            Video video = videoMapper.selectById(task.getVideoId());
            if (video != null) {
                taskVOList.get(i).setVideo(BeanCopyUtil.copy(video, VideoVO.class));
            }
        }
        
        voPage.setRecords(taskVOList);
        
        return voPage;
    }
    
    /**
     * 更新任务状态（供内部回调使用）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskStatus(String taskId, String status, String errorDetails) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        
        task.setStatus(status);
        if ("completed".equals(status) || "failed".equals(status)) {
            task.setCompletedAt(LocalDateTime.now());
        }
        if (errorDetails != null) {
            task.setErrorDetails(errorDetails);
        }
        
        taskMapper.updateById(task);
        log.info("任务状态更新 {} -> {}", taskId, status);
    }
}

