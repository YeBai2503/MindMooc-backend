package com.mindmooc.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户视图对象
 */
@Data
public class UserVO {
    
    /**
     * 用户ID
     */
    private String id;
    
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
    
    /**
     * 角色 (0=普通用户, 1=管理员)
     */
    private Integer role;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

