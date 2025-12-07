package com.mindmooc.business;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;

@SpringBootTest
public class DiscoveryTest {

    @Autowired
    DiscoveryClient discoveryClient;

    @Test
    public void test() {
        discoveryClient.getServices().forEach(serviceName -> {
            System.out.println("服务名：" + serviceName);
            discoveryClient.getInstances(serviceName).forEach(instance -> {
                System.out.println(instance.getHost() + ":" + instance.getPort());
            });
        });
    }

}
