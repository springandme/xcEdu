package com.xuecheng.govern.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ClassName GorvernCenterApplication
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/30 18:04
 * @Version V1.0
 **/
//标识此工程是一个EurekaServer
@EnableEurekaServer
@SpringBootApplication
public class GorvernCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(GorvernCenterApplication.class, args);
    }
}
