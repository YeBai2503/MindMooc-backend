package com.mindmooc.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindmooc.business.mapper.FeedbackMapper;
import com.mindmooc.business.utils.BeanCopyUtil;
import com.mindmooc.common.BusinessException;
import com.mindmooc.common.ResultCode;
import com.mindmooc.dto.CreateFeedbackRequest;
import com.mindmooc.entity.Feedback;
import com.mindmooc.vo.FeedbackVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 反馈服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {
    
    private final FeedbackMapper feedbackMapper;
    
    /**
     * 创建反馈
     */
    @Transactional(rollbackFor = Exception.class)
    public FeedbackVO createFeedback(String userId, CreateFeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId); // 可以为空，支持匿名反馈
        feedback.setType(request.getType());
        feedback.setDetails(request.getDetails());
        feedback.setStatus("open"); // 默认为open
        feedback.setCreatedAt(LocalDateTime.now());
        
        feedbackMapper.insert(feedback);
        
        log.info("反馈创建成功: {}", feedback.getId());
        return BeanCopyUtil.copy(feedback, FeedbackVO.class);
    }
    
    /**
     * 根据ID获取反馈
     */
    public FeedbackVO getFeedbackById(String feedbackId) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "反馈不存在");
        }
        return BeanCopyUtil.copy(feedback, FeedbackVO.class);
    }
    
    /**
     * 获取用户的反馈列表（分页）
     */
    public IPage<FeedbackVO> getUserFeedbacks(String userId, int pageNum, int pageSize) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId)
               .orderByDesc(Feedback::getCreatedAt);
        
        IPage<Feedback> feedbackPage = feedbackMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<FeedbackVO> voPage = new Page<>(feedbackPage.getCurrent(), feedbackPage.getSize(), feedbackPage.getTotal());
        voPage.setRecords(BeanCopyUtil.copyList(feedbackPage.getRecords(), FeedbackVO.class));
        
        return voPage;
    }
    
    /**
     * 获取所有反馈列表（分页，管理员使用）
     */
    public IPage<FeedbackVO> getAllFeedbacks(int pageNum, int pageSize, String status) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Feedback::getStatus, status);
        }
        
        wrapper.orderByDesc(Feedback::getCreatedAt);
        
        IPage<Feedback> feedbackPage = feedbackMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<FeedbackVO> voPage = new Page<>(feedbackPage.getCurrent(), feedbackPage.getSize(), feedbackPage.getTotal());
        voPage.setRecords(BeanCopyUtil.copyList(feedbackPage.getRecords(), FeedbackVO.class));
        
        return voPage;
    }
    
    /**
     * 更新反馈状态（管理员使用）
     */
    @Transactional(rollbackFor = Exception.class)
    public FeedbackVO updateFeedbackStatus(String feedbackId, String status) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "反馈不存在");
        }
        
        feedback.setStatus(status);
        feedbackMapper.updateById(feedback);
        
        log.info("反馈状态更新成功！ {} -> {}", feedbackId, status);
        return BeanCopyUtil.copy(feedback, FeedbackVO.class);
    }
}

