package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程管理接口", description = "课程管理接口，提供课程内容的增、删、改、查")
public interface CourseControllerApi {

    @ApiOperation("课程计划查询")
    TeachplanNode findTeachPlanList(String courseId);

    @ApiOperation("查询我的课程列表")
    QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("添加课程计划")
    ResponseResult addTeachPlan(Teachplan teachplan);

    @ApiOperation("添加课程基础信息")
    AddCourseResult addCourseBase(CourseBase courseBase);

    @ApiOperation("获取课程基础信息")
    CourseBase getCourseBaseById(String courseId) throws RuntimeException;

    @ApiOperation("更新课程基础信息")
    ResponseResult updateCourseBase(String id, CourseBase courseBase);

    @ApiOperation("获取课程营销信息")
    CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("更新课程营销信息")
    ResponseResult updateCourseMarket(String id, CourseMarket courseMarket);

    @ApiOperation("保存课程图片信息")
    ResponseResult saveCoursePic(String courseId, String pic);

    @ApiOperation("获得课程图片信息")
    CoursePic getCoursePic(String courseId);

    @ApiOperation("删除课程图片信息")
    ResponseResult deleteCoursePic(String courseId);

    @ApiOperation("课程视图查询")
    CourseView getCourseView(String id);

    @ApiOperation("课程预览")
    CoursePublishResult coursePreview(String courseId);

    @ApiOperation("课程发布")
    CoursePublishResult coursePublish(String courseId);

    @ApiOperation("保存课程计划与媒资文件关联")
    ResponseResult saveMedia(TeachplanMedia teachplanMedia);

    // @ApiOperation("查询指定公司下的所有课程")
    // QueryResponseResult<CourseInfo> findCourseListByCompany(int page, int size, CourseListRequest courseListRequest);
}
