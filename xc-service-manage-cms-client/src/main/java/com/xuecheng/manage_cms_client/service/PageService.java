package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * @ClassName PageService
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/26 18:02
 * @Version V1.0
 **/
@Service
public class PageService {

    //记录日志
    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 保存HTML页面到服务器的物理路径
     *
     * @param pageId 页面id
     */
    public void savePageToServicePath(String pageId) {
        //根据pageId查询CMSPage
        CmsPage cmsPage = this.findCmsPageById(pageId);
        //先得到html文件的id,从CMSPage中获得htmlFiled内容
        String htmlFileId = cmsPage.getHtmlFileId();

        //  1.从gridFS中查询html文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if (inputStream == null) {
            LOGGER.error("getFileById InputStream is null ,htmlFileId:{}", htmlFileId);
            return;
        }
        //得到站点id
        String siteId = cmsPage.getSiteId();
        //得到站点信息
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        //得到站点的物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();

        //得到页面的物理路径
        String pagePath = sitePhysicalPath + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();

        //  2.将html文件保存到服务器物理路径上
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件id从GridFS中查询文件内容
     *
     * @param htmlFileId CMSPage中的字段html的id
     * @return InputStream
     */
    public InputStream getFileById(String htmlFileId) {
        //  文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        //  打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //  定义GridFsResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据id查询页面信息
     *
     * @param pageId 页面id
     * @return CMSPage
     */
    public CmsPage findCmsPageById(String pageId) {
        Optional<CmsPage> one = cmsPageRepository.findById(pageId);
        if (one.isPresent()) {
            return one.get();
        }
        return null;
    }

    /**
     * 根也站点id查询站点信息[cmsSite]
     *
     * @param siteId 站点id
     * @return 站点信息
     */
    public CmsSite findCmsSiteById(String siteId) {
        Optional<CmsSite> one = cmsSiteRepository.findById(siteId);
        if (one.isPresent()) {
            return one.get();
        }
        return null;
    }
}
