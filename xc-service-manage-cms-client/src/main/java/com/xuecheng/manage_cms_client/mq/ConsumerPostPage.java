package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName ConsumerPostPage
 * @Description 监听MQ, 接受页面发布消息
 * @Author liushi
 * @Date 2020/7/26 18:48
 * @Version V1.0
 **/
@Component
public class ConsumerPostPage {

    //记录日志
    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    private PageService pageService;

    //从配置文件application.yml中获取
    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg) {
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        //得到消息中的页面id
        String pageId = (String) map.get("pageId");
        //校验页面是否合法
        CmsPage cmsPageById = pageService.findCmsPageById(pageId);
        if (cmsPageById == null) {
            LOGGER.error("receive postpage msg,cmsPage is null,pageId:{}", pageId);
            return;
        }

        //调用service方法将页面从GridFS中下载到服务器
        pageService.savePageToServicePath(pageId);
    }
}
