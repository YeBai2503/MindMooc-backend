package com.mindmooc.dto;

import lombok.Data;

/**
 * 更新用户信息请求 DTO
 */
@Data
public class UpdateUserRequest {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
}

