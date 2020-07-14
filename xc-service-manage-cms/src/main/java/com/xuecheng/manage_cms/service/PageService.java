package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

/**
 * @ClassName PageServiceImpl
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/5 16:33
 * @Version V1.0
 **/
@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;


    /**
     * @return com.xuecheng.framework.model.response.QueryResponseResult
     * @Description 页面查询方法,
     * @Date 2020-07-05 16:41
     * @Param page 页码,从1开始
     * @Param QueryPageRequest 查询条件
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }

        //自定义条件查询
        //定义条件匹配器
        ExampleMatcher exampleMatcher =
                ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //设置站点id作为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置模板id作为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //谁知页面别名为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //定义条件对象
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

        //分页参数
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        //实现自定义条件查询,和条件查询   -->当example里面的值,为空时, findAll(example, pageable)就变成了findAll(pageable)
        Page<CmsPage> pages = cmsPageRepository.findAll(example, pageable);
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        //数据列表
        queryResult.setList(pages.getContent());
        //数据总记录数
        queryResult.setTotal(pages.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
     * @Author liushi
     * @Description 新增页面
     * @Date 2020-07-13 14:15
     * @Param [cmsPage]
     */
    public CmsPageResult add(CmsPage cmsPage) {
        //校验页面名称,站点Id,页面webPath的唯一性
        //根据页面名称,站点Id,页面webPath去cms_page集合,如果查询到说明页面已经存在,如果查询不到再继续添加.

        CmsPage cmsPage1 =
                cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(),
                        cmsPage.getPageWebPath());
        if (cmsPage1 == null) {
            //调用dao新增页面
            //把主键设置为空,MongoDb会自动生成主键
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        }
        //添加失败!
        return new CmsPageResult(CommonCode.FAIL, null);
    }


}
