package com.mindmooc.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mindmooc.business.service.FeedbackService;
import com.mindmooc.common.Result;
import com.mindmooc.dto.CreateFeedbackRequest;
import com.mindmooc.vo.FeedbackVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈控制类
 */
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
    /**
     * 创建反馈
     */
    @PostMapping
    public Result<FeedbackVO> createFeedback(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateFeedbackRequest request) {
        FeedbackVO feedback = feedbackService.createFeedback(userId, request);
        return Result.success("反馈提交成功", feedback);
    }
    
    /**
     * 获取反馈详情
     */
    @GetMapping("/{feedbackId}")
    public Result<FeedbackVO> getFeedbackById(@PathVariable String feedbackId) {
        FeedbackVO feedback = feedbackService.getFeedbackById(feedbackId);
        return Result.success(feedback);
    }
    
    /**
     * 获取用户的反馈列表（分页）
     */
    @GetMapping("/my")
    public Result<IPage<FeedbackVO>> getUserFeedbacks(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<FeedbackVO> feedbackPage = feedbackService.getUserFeedbacks(userId, pageNum, pageSize);
        return Result.success(feedbackPage);
    }
    
    /**
     * 获取所有反馈列表（分页，管理员使用）
     */
    @GetMapping("/all")
    public Result<IPage<FeedbackVO>> getAllFeedbacks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        IPage<FeedbackVO> feedbackPage = feedbackService.getAllFeedbacks(pageNum, pageSize, status);
        return Result.success(feedbackPage);
    }
    
    /**
     * 更新反馈状态（管理员使用）
     */
    @PutMapping("/{feedbackId}/status")
    public Result<FeedbackVO> updateFeedbackStatus(
            @PathVariable String feedbackId,
            @RequestParam String status) {
        FeedbackVO feedback = feedbackService.updateFeedbackStatus(feedbackId, status);
        return Result.success("状态更新成功", feedback);
    }
}

