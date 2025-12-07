package com.mindmooc.business;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class BusinessMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessMainApplication.class, args);
    }
}
