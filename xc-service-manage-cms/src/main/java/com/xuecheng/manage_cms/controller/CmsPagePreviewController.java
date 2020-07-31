package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-15 16:20
 **/
@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    PageService pageService;

    //页面预览
    @RequestMapping(value = "/cms/preview/{pageId}", method = RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId) throws IOException {
        //执行静态化
        String pageHtml = pageService.getPageHtml(pageId);
        //通过response对象将内容输出
        ServletOutputStream outputStream = response.getOutputStream();
        // 由于Nginx先请求cms课程预览功能得到HTML页面,在解析界面中的试试标签,这里必须保证cms页面预览返回的Content-Type为text/html;charset=utf-8
        response.setHeader("Content-type", "text/html;charset=utf-8");
        outputStream.write(pageHtml.getBytes("utf-8"));

    }
}
