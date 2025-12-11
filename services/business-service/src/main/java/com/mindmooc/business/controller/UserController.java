package com.mindmooc.business.controller;

import com.mindmooc.business.service.UserService;
import com.mindmooc.common.Result;
import com.mindmooc.dto.LoginRequest;
import com.mindmooc.dto.RegisterRequest;
import com.mindmooc.dto.UpdateUserRequest;
import com.mindmooc.vo.LoginResponse;
import com.mindmooc.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制类
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        UserVO user = userService.register(request);
        return Result.success("注册成功", user);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(@RequestHeader("X-User-Id") String userId) {
        UserVO user = userService.getUserById(userId);
        return Result.success(user);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserVO> getUserById(@PathVariable String userId) {
        UserVO user = userService.getUserById(userId);
        return Result.success(user);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/me")
    public Result<UserVO> updateUser(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserVO user = userService.updateUser(userId, request);
        return Result.success("更新成功", user);
    }
}

