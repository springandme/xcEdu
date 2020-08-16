package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CourseController
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/27 16:22
 * @Version V1.0
 **/
@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {

    @Autowired
    private CourseService courseService;

    // 当用户拥有course_teachplan_list权限时候,方可访问此方法
    @PreAuthorize("hasAuthority('course_teachplan_list')")
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachPlanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachPlanList(courseId);
    }

    /**
     * 根据查询条件查询我的课程列表
     *
     * @param page              页码
     * @param size              记录
     * @param courseListRequest 查询条件
     * @return QueryResponseResult
     */
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult<CourseInfo> findCourseList(@PathVariable("page") int page,
                                                          @PathVariable("size") int size,
                                                          CourseListRequest courseListRequest) {
        // 获取当前用户信息
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        // 当前用户所属单位的id
        String companyId = userJwt.getCompanyId();
        return courseService.findCourseList(page, size, companyId, courseListRequest);
    }

    @PreAuthorize("hasAuthority('course_teachplan_add')")
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachPlan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachPlan(teachplan);
    }

    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseResult(courseBase);
    }

    @Override
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) throws RuntimeException {
        return courseService.getCourseBaseById(courseId);
    }

    @Override
    @PutMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBase(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(id, courseBase);
    }

    @Override
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    @Override
    @PutMapping("/coursemarket/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        CourseMarket updateCourseMarket = courseService.updateCourseMarket(id, courseMarket);
        if (updateCourseMarket != null) {
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult saveCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        return courseService.addCoursePic(courseId, pic);
    }

    // 当用户拥有course_pic_list权限时候,方可访问此方法
    @PreAuthorize("hasAuthority('course_pic_list')")
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic getCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.getCoursePic(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView getCourseView(@PathVariable("id") String id) {
        return courseService.getCourseView(id);
    }

    @Override
    @PostMapping("/preview/{courseId}")
    public CoursePublishResult coursePreview(@PathVariable("courseId") String courseId) {
        return courseService.coursePreview(courseId);
    }

    @Override
    @PostMapping("/publish/{courseId}")
    public CoursePublishResult coursePublish(@PathVariable("courseId") String courseId) {
        return courseService.coursePublish(courseId);
    }

    @Override
    @PostMapping("/savemedia")
    public ResponseResult saveMedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.saveMedia(teachplanMedia);
    }

}
