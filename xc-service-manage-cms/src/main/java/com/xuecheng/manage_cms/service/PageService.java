package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
                ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains())
                        .withMatcher("pageName", ExampleMatcher.GenericPropertyMatchers.contains());
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
        //设置页面别名为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //设置页面名称为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getPageName())) {
            cmsPage.setPageName((queryPageRequest.getPageName()));
        }
        //设置页面类型为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getPageType())) {
            cmsPage.setPageType(queryPageRequest.getPageType());
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
   /* public CmsPageResult add(CmsPage cmsPage) {
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
    }*/

    /**
     * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
     * @Author liushi
     * @Description 新增页面
     * @Date 2020-07-13 14:15
     * @Param [cmsPage]
     */
    public CmsPageResult add(CmsPage cmsPage) {
        if (cmsPage == null) {
            //抛出异常,非法参数异常,
            ExceptionCast.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }

        //校验页面名称,站点Id,页面webPath的唯一性
        //根据页面名称,站点Id,页面webPath去cms_page集合,如果查询到说明页面已经存在,如果查询不到再继续添加.

        CmsPage cmsPage1 =
                cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(),
                        cmsPage.getPageWebPath());
        if (cmsPage1 != null) {
            //页面已经存在
            //拖出异常,异常内容就是页面已经存在
//            throw new CustomException(CommonCode.FAIL);
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //调用dao新增页面
        //把主键设置为空,MongoDb会自动生成主键
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);

        //添加失败!
        //return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * @return com.xuecheng.framework.domain.cms.CmsPage
     * @Author liushi
     * @Description 根据id从数据查询页面信息
     * @Date 2020-07-14 20:56
     * @Param [id]
     */
    public CmsPage getById(String id) {
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }


    /**
     * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
     * @Author liushi
     * @Description //TODO
     * @Date 2020-07-14 21:01
     * @Param [id, cmsPage]
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //根据id从数据查询页面信息
        CmsPage one = this.getById(id);
        if (one != null) {
            //准备更新数据
            //设置要修改的数据
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //执行更新
            cmsPageRepository.save(one);
            return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }


    /**
     * @return com.xuecheng.framework.model.response.ResponseResult
     * @Author liushi
     * @Description 删除页面
     * @Date 2020-07-14 21:56
     * @Param [id]
     */
    public ResponseResult delete(String id) {
        //先查询一下
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(id);
        if (optionalCmsPage.isPresent()) {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * @return com.xuecheng.framework.domain.cms.CmsConfig
     * @Author liushi
     * @Description 根据id查询CmsConfig
     * @Date 2020-07-19 13:15
     * @Param [id]
     */
    public CmsConfig getConfigById(String id) {
        Optional<CmsConfig> one = cmsConfigRepository.findById(id);
        if (one.isPresent()) {
            return one.get();
        }
        return null;
    }


    /**
     * @return java.lang.String
     * @Author liushi
     * @Description 页面静态化方法
     * @Date 2020-07-20 20:13
     * @Param [pageId]
     */
    public String getPageHtml(String pageId) {
        //获取数据模型
        Map model = this.getModelByPageId(pageId);
        if (model == null) {
            //数据模型获取不到
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //获取页面的模板信息
        String template = this.getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(template)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String generateHtml = this.generateHtml(template, model);
        return generateHtml;
    }


    /**
     * @return java.lang.String
     * @Author liushi
     * @Description 执行静态化
     * @Date 2020-07-20 20:40
     * @Param []
     */
    private String generateHtml(String templateContent, Map model) {
        //创建一个配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", templateContent);
        //向configuration配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template template = configuration.getTemplate("template");
            //调用API进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @return java.lang.String
     * @Author liushi
     * @Description 获取页面的模板信息
     * @Date 2020-07-20 20:25
     * @Param [pageId]
     */
    private String getTemplateByPageId(String pageId) {
        //取出页面的信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取页面的模板id
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从GridFS中取模板文件内容
            //根据文件id查询文件
            GridFSFile gridFSFile =
                    gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource对象,获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            //从流中取数据
            try {
                return IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @return java.util.Map
     * @Author liushi
     * @Description 获取数据模型
     * @Date 2020-07-20 20:15
     * @Param [pageId]
     */
    private Map getModelByPageId(String pageId) {
        //取出页面的信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出页面的dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            //页面dataUrl为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //通过restTemplate请求dataUrl获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        return forEntity.getBody();
    }

    /**
     * 页面发布
     *
     * @param pageId 页面id
     * @return ResponseResult
     */
    public ResponseResult post(String pageId) {
        //指定页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将页面静态化文件存储到GridFS中
        CmsPage cmsPage = this.saveHtml(pageId, pageHtml);
        //向mq发消息
        this.sendPostPage(pageId);

        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 向mq发送消息
     *
     * @param pageId 页面id
     */
    private void sendPostPage(String pageId) {
        //先得到页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        //创建消息对象
        Map<String, String> msg = new HashMap<>();
        msg.put("pageId", pageId);
        //转为json串
        String jsonString = JSON.toJSONString(msg);
        //发送给mq
        //  站点id
        String siteId = cmsPage.getSiteId();
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, jsonString);
    }


    /**
     * 保存html到GridFS
     *
     * @param pageId      页面id
     * @param htmlContent 页面内容
     * @return CMSPage
     */
    private CmsPage saveHtml(String pageId, String htmlContent) {
        //先得到页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        InputStream inputStream = null;
        ObjectId objectId = null;

        try {
            //将htmlContent内容转成输入流
            inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件内容保存到GridFS
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //将html文件id更新到cmsPage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);

        return cmsPage;
    }


    /**
     * 保存页面,有就更新,没有则添加
     *
     * @param cmsPage 页面信息
     * @return CmsPageResult
     */
    public CmsPageResult save(CmsPage cmsPage) {
        //判断页面是否存在
        CmsPage one =
                cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(),
                        cmsPage.getPageWebPath());
        if (one != null) {
            //进行更新
            return this.update(one.getPageId(), one);
        }
        return add(cmsPage);
    }
}
