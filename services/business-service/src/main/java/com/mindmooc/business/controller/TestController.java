package com.mindmooc.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RequestMapping("/api/business") //网关中已经添加过滤器处理这个冗余路径

/**
 * Test接口
 */

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "测试成功";
    }
}
