package com.xuecheng.manage_course.client;


import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//指定远程调用的服务名
@FeignClient(value = "XC-SERVICE-MANAGE-CMS")
public interface CmsPageClient {


    /**
     * 根据页面id查询页面信息,远程调用cms请求数据
     *
     * @param id 页面id
     * @return CmsPage
     */
    //用GetMapping标识远程调用的http的方法类型
    @GetMapping("/cms/page/get/{id}")
    CmsPage findCmsPageById(@PathVariable("id") String id);


    /**
     * 添加页面,用于课程预览
     *
     * @param cmsPage 页面信息
     * @return CmsPageResult
     */
    @PostMapping("/cms/page/save")
    CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage);
}
