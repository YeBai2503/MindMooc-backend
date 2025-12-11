package com.mindmooc.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})//禁止自动配置数据源
public class AiMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiMainApplication.class, args);
    }
}
