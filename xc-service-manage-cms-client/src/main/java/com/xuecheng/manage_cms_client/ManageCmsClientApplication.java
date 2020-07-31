package com.xuecheng.manage_cms_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName ManageCmsClientApplication
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/26 17:47
 * @Version V1.0
 **/
//标识一个EurekaClient从EurekaServer发现服务
@EnableDiscoveryClient
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")  //扫描实体类
@ComponentScan(basePackages = {"com.xuecheng.framework"})//扫描common下的所有类
@ComponentScan(basePackages = {"com.xuecheng.manage_cms_client"})
public class ManageCmsClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsClientApplication.class, args);
    }
}
