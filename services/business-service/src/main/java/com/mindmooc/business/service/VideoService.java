package com.mindmooc.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindmooc.business.config.FileUploadConfig;
import com.mindmooc.business.mapper.VideoMapper;
import com.mindmooc.business.utils.BeanCopyUtil;
import com.mindmooc.business.utils.FileUtil;
import com.mindmooc.common.BusinessException;
import com.mindmooc.common.ResultCode;
import com.mindmooc.entity.Video;
import com.mindmooc.vo.VideoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {
    
    private final VideoMapper videoMapper;
    private final FileUploadConfig fileUploadConfig;
    
    /**
     * 上传视频
     */
    @Transactional(rollbackFor = Exception.class)
    public VideoVO uploadVideo(MultipartFile file, String videoTitle) {
        // 验证文件
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.VIDEO_UPLOAD_FAILED.getCode(), "文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        
        // 验证文件类型
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new BusinessException(ResultCode.VIDEO_UPLOAD_FAILED.getCode(), "只能上传视频文件");
        }
        
        try {
            // 保存到临时文件
            String uniqueFilename = FileUtil.generateUniqueFileName(originalFilename);
            String uploadPath = fileUploadConfig.getPath();
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            File tempFile = new File(uploadPath + uniqueFilename);
            file.transferTo(tempFile);
            
            // 计算文件哈希值（用于去重）
            String fileHash = FileUtil.calculateFileHash(tempFile);
            
            // 检查文件是否已存在
            LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Video::getFileHash, fileHash);
            Video existingVideo = videoMapper.selectOne(wrapper);
            
            if (existingVideo != null) {
                // 文件已存在，删除临时文件
                FileUtil.deleteFile(tempFile.getAbsolutePath());
                log.info("视频已存在，返回已有记录: {}", existingVideo.getId());
                return BeanCopyUtil.copy(existingVideo, VideoVO.class);
            }
            
            // 创建视频记录
            Video video = new Video();
            video.setFileHash(fileHash);
            video.setStorageUrl(fileUploadConfig.getBaseUrl() + uniqueFilename);
            video.setOriginalFilename(originalFilename);
            video.setVideoTitle(videoTitle != null ? videoTitle : originalFilename);
            video.setFileSize(file.getSize());
            video.setMimeType(contentType);
            video.setDuration(0); // 默认0，后续可以通过视频分析获取
            video.setCreatedAt(LocalDateTime.now());
            
            videoMapper.insert(video);
            
            log.info("视频上传成功: {}", video.getId());
            return BeanCopyUtil.copy(video, VideoVO.class);
            
        } catch (IOException e) {
            log.error("视频上传失败", e);
            throw new BusinessException(ResultCode.VIDEO_UPLOAD_FAILED);
        }
    }
    
    /**
     * 根据ID获取视频信息
     */
    public VideoVO getVideoById(String videoId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            throw new BusinessException(ResultCode.VIDEO_NOT_FOUND);
        }
        return BeanCopyUtil.copy(video, VideoVO.class);
    }
    
    /**
     * 分页查询视频列表
     */
    public IPage<VideoVO> getVideoList(int pageNum, int pageSize) {
        Page<Video> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Video::getCreatedAt);
        
        IPage<Video> videoPage = videoMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<VideoVO> voPage = new Page<>(videoPage.getCurrent(), videoPage.getSize(), videoPage.getTotal());
        voPage.setRecords(BeanCopyUtil.copyList(videoPage.getRecords(), VideoVO.class));
        
        return voPage;
    }
    
    /**
     * 删除视频
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(String videoId) {
        Video video = videoMapper.selectById(videoId);
        if (video == null) {
            throw new BusinessException(ResultCode.VIDEO_NOT_FOUND);
        }
        
        // 删除文件
        String storageUrl = video.getStorageUrl();
        String filename = storageUrl.substring(storageUrl.lastIndexOf('/') + 1);
        String filePath = fileUploadConfig.getPath() + filename;
        FileUtil.deleteFile(filePath);
        
        // 删除数据库记录
        videoMapper.deleteById(videoId);
        
        log.info("视频删除成功: {}", videoId);
    }
}

