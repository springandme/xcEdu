package com.xuecheng.search.controller;

import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {
    @Autowired
    EsCourseService esCourseService;

    @Override
    @GetMapping(value = "/list/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") int page, @PathVariable("size") int size,
                                               CourseSearchParam courseSearchParam) {
        return esCourseService.list(page, size, courseSearchParam);
    }

    @Override
    @GetMapping("/getall/{courseId}")
    public Map<String, CoursePub> getAll(@PathVariable("courseId") String courseId) {
        return esCourseService.getAll(courseId);
    }

    @Override
    @GetMapping("/getmedia/{teachplanId}")
    public TeachplanMediaPub getMedia(@PathVariable("teachplanId") String teachplanId) {
        // 为了service的拓展性,所以我们service接收的是数组作为参数,以便后续开发查询多个ID的接口
        String[] teachplanIds = new String[]{teachplanId};
        // 通过service查询ES获取课程媒资信息
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = esCourseService.getMedia(teachplanIds);
        QueryResult<TeachplanMediaPub> queryResult = queryResponseResult.getQueryResult();
        if (queryResult != null) {
            List<TeachplanMediaPub> list = queryResult.getList();
            if (list != null && list.size() > 0) {
                // 返回课程计划对应课程媒资
                return list.get(0);
            }
        }
        return new TeachplanMediaPub();
    }
}
