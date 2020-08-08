package com.xuecheng.manage_cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @ClassName RestTemplateTest
 * @Description 2020年7月19日16:47:21--测试不通过原因,没有开启ManagerCmsApplication启动类,开启31001服务
 * @Author liushi
 * @Date 2020/7/19 16:21
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class RestTemplateTest {

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void testRestTemplate() {
        ResponseEntity<Map> forEntity =
                restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f",
                        Map.class);
        Map body = forEntity.getBody();
        System.out.println(body);


    }
}
