package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "文件管理接口", description = "文件管理接口，提供对文件的CRUD")
public interface FileSystemControllerApi {

    //文件上传（MultipartFile）->采用springmvc框架实现上传
    @ApiOperation("上传文件接口")
    UploadFileResult uploadFile(MultipartFile multipartFile, String fileTage, String businessKey, String metaData);
}
