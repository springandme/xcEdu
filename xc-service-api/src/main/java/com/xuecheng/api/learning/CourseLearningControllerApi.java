package com.xuecheng.api.learning;

import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "录播课程学习管理", description = "录播课程学习管理")
public interface CourseLearningControllerApi {

    @ApiOperation("课程课程学习地址")
    GetMediaResult getMedia(String courseId, String teachplanId);
}
