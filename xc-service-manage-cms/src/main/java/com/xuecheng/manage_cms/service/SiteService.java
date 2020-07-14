package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName SiteServiceImpl
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/13 12:02
 * @Version V1.0
 **/
@Service
public class SiteService {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    /**
     * @return com.xuecheng.framework.domain.cms.CmsSite
     * @Author liushi
     * @Description 从数据中查询出所有站点
     * @Date 2020-07-13 12:00
     * @Param []
     */
    public QueryResponseResult findAll() {
        List<CmsSite> list = cmsSiteRepository.findAll();
        QueryResult<CmsSite> queryResult = new QueryResult<>();
        queryResult.setList(list);
        queryResult.setTotal(list.size());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }
}
