package com.xuecheng.manage_cms;

import com.xuecheng.manage_cms.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName PageServiceTest
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/20 20:53
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {

    @Autowired
    private PageService pageService;

    @Test
    public void generateHtmlTest() {
        //5f14ec68b5a8f8e8dc251808
        String pageHtml = pageService.getPageHtml("5f0c3d020f3ea43100b9f314");
        System.out.println(pageHtml);
    }
}
