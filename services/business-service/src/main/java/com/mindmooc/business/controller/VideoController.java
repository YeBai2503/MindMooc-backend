package com.mindmooc.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mindmooc.business.service.VideoService;
import com.mindmooc.common.Result;
import com.mindmooc.vo.VideoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频控制类
 */
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {
    
    private final VideoService videoService;
    
    /**
     * 上传视频
     */
    @PostMapping("/upload")
    public Result<VideoVO> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "videoTitle", required = false) String videoTitle) {
        VideoVO video = videoService.uploadVideo(file, videoTitle);
        return Result.success("上传成功", video);
    }
    
    /**
     * 获取视频信息
     */
    @GetMapping("/{videoId}")
    public Result<VideoVO> getVideoById(@PathVariable String videoId) {
        VideoVO video = videoService.getVideoById(videoId);
        return Result.success(video);
    }
    
    /**
     * 获取视频列表（分页）
     */
    @GetMapping("/list")
    public Result<IPage<VideoVO>> getVideoList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<VideoVO> videoPage = videoService.getVideoList(pageNum, pageSize);
        return Result.success(videoPage);
    }
    
    /**
     * 删除视频
     */
    @DeleteMapping("/{videoId}")
    public Result<Void> deleteVideo(@PathVariable String videoId) {
        videoService.deleteVideo(videoId);
        return Result.success("删除成功", null);
    }
}

