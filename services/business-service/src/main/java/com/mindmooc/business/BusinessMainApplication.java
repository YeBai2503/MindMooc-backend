package com.mindmooc.business;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.mindmooc.business.mapper")
@SpringBootApplication(scanBasePackages = {"com.mindmooc"})
public class BusinessMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessMainApplication.class, args);
    }
}
