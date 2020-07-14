package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CmsPageController
 * @Description 测试Controller
 * @Author liushi
 * @Date 2020/7/5 12:28
 * @Version V1.0
 **/
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    private PageService pageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size,
                                        QueryPageRequest queryPageRequest) {
        /*
        //暂时用静态数据
        //定义QueryResult
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        List<CmsPage> list = new ArrayList<>();
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试首页");
        list.add(cmsPage);
        queryResult.setList(list);
        queryResult.setTotal(1);

        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        */

        return pageService.findList(page, size, queryPageRequest);
    }


    //@RequestBody 可以将请求过得json数据转换为java对象CmsPage
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }
}
