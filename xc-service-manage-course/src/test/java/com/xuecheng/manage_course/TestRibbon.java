package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {

    @Autowired
    private RestTemplate restTemplate;

    //cmsPageClient接口代理对象,由Feign来生成代理对象
    @Autowired
    private CmsPageClient cmsPageClient;


    //使用restTemplate发起http远程和ribbon远程调用cms服务
    @Test
    public void testRibbon() {
        //确定要获取的服务名称
        String serviceId = "XC-SERVICE-MANAGE-CMS";

        for (int i = 0; i < 10; i++) {
            //ribbon客户端从EurekaServer中获取服务列表,根据服务名获取服务列表
            ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://" + serviceId + "/cms/page/get" +
                    "/5a754adf6abb500ad05688d9", Map.class);
            Map body = forEntity.getBody();
            System.out.println(body);
        }
    }

    @Test
    public void testFeignClientRibbon() {
        //发起远程调用
        CmsPage cmsPage = cmsPageClient.findCmsPageById("5a754adf6abb500ad05688d9");
        System.out.println(cmsPage);

    }
}
