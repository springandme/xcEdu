package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程搜索", description = "基于ES构建的课程搜索API", tags = {"课程搜索"})
public interface EsCourseControllerApi {

    /**
     * 搜索课程信息
     *
     * @param page              页码
     * @param size              每页显示记录
     * @param courseSearchParam 查询条件
     * @return QueryResponseResult<CoursePub>
     */
    @ApiOperation("课程综合搜索")
    QueryResponseResult<CoursePub> findList(int page, int size, CourseSearchParam courseSearchParam);
}
