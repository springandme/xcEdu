package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @ClassName ManageCmsApplication
 * @Description 启动类
 * @Author liushi
 * @Date 2020/7/5 12:15
 * @Version V1.0
 **/
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms")//扫描实体类
@ComponentScan(basePackages = {"com.xuecheng.api"})//扫描接口
@ComponentScan(basePackages = {"com.xuecheng.manage_cms"})//扫描本项目下的所有类
//@EnableScheduling
public class ManageCmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class);
    }
}
