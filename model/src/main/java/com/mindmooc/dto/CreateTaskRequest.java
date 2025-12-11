package com.mindmooc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建任务请求 DTO
 */
@Data
public class CreateTaskRequest {
    
    /**
     * 视频ID
     */
    @NotBlank(message = "视频ID不能为空")
    private String videoId;
    
    /**
     * 任务类型（默认为 common）
     */
    private String taskType = "common";
}

