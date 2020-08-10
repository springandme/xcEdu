package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "媒体文件管理", description = "媒体文件管理接口", tags = {"媒体文件管理接口"})
public interface MediaFileControllerApi {


    /**
     * 媒资文件查询列表
     *
     * @param page                  页码
     * @param size                  条数
     * @param queryMediaFileRequest 查询条件
     * @return QueryResponseResult
     */
    @ApiOperation("媒资文件查询列表")
    QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest);
}
