package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName MediaFileService
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/10 18:09
 * @Version V1.0
 **/
@Service
public class MediaFileService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    /**
     * 媒资文件查询列表
     *
     * @param page                  页码
     * @param size                  条数
     * @param queryMediaFileRequest 查询条件
     * @return QueryResponseResult
     */
    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        // 定义条件值对象
        MediaFile mediaFile = new MediaFile();
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())) {
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())) {
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())) {
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        // 查询条件匹配器  如果不设置匹配器,默认为精确匹配exact()
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());

        // 定义example条件对象
        Example<MediaFile> example = Example.of(mediaFile, exampleMatcher);


        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        // 分页查询对象
        Pageable pageable = new PageRequest(page, size);

        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        List<MediaFile> mediaFileList = all.getContent();
        long total = all.getTotalElements();

        // 设置响应对象属性
        QueryResult<MediaFile> mediaFileQueryResult = new QueryResult<>();
        mediaFileQueryResult.setList(mediaFileList);
        mediaFileQueryResult.setTotal(total);
        return new QueryResponseResult<>(CommonCode.SUCCESS, mediaFileQueryResult);
    }
}
