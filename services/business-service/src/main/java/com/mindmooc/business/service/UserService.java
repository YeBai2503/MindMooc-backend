package com.mindmooc.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindmooc.business.mapper.UserMapper;
import com.mindmooc.business.utils.BeanCopyUtil;
import com.mindmooc.business.utils.JwtUtil;
import com.mindmooc.common.BusinessException;
import com.mindmooc.common.ResultCode;
import com.mindmooc.dto.LoginRequest;
import com.mindmooc.dto.RegisterRequest;
import com.mindmooc.dto.UpdateUserRequest;
import com.mindmooc.entity.User;
import com.mindmooc.vo.LoginResponse;
import com.mindmooc.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;// 在SecurityConfig类中配置了使用BCrypt加密
    private final JwtUtil jwtUtil;
    
    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }
        
        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new BusinessException(400, "邮箱已被使用");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(0); // 默认为普通用户
        
        userMapper.insert(user);
        
        log.info("用户注册成功: {}", user.getUsername());
        return BeanCopyUtil.copy(user, UserVO.class);
    }
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户（支持用户名或邮箱登录）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername())
               .or()
               .eq(User::getEmail, request.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ResultCode.INVALID_USERNAME_OR_PASSWORD);
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.INVALID_USERNAME_OR_PASSWORD);
        }
        
        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        UserVO userVO = BeanCopyUtil.copy(user, UserVO.class);
        
        log.info("用户登录成功: {}", user.getUsername());
        return new LoginResponse(token, userVO);
    }
    
    /**
     * 根据ID获取用户信息
     */
    public UserVO getUserById(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return BeanCopyUtil.copy(user, UserVO.class);
    }
    
    /**
     * 更新用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateUser(String userId, UpdateUserRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 更新用户名（如果提供且不同）
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            // 检查新用户名是否已存在
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, request.getUsername());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
            }
            user.setUsername(request.getUsername());
        }
        
        // 更新邮箱（如果提供且不同)
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // 检查新邮箱是否已存在
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, request.getEmail());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(400, "邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }
        
        // 更新头像
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        userMapper.updateById(user);
        
        log.info("用户信息更新成功: {}", user.getUsername());
        return BeanCopyUtil.copy(user, UserVO.class);
    }
}

