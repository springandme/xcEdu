package com.xuecheng.learning.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName CourseSearchClient
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/12 16:13
 * @Version V1.0
 **/
@FeignClient(value = XcServiceList.XC_SERVICE_SEARCH)
public interface CourseSearchClient {

    /**
     * 根据课程假话id查询课程媒资
     *
     * @param teachplanId 课程计划id
     * @return TeachplanMediaPub
     */
    @GetMapping("/search/course/getmedia/{teachplanId}")
    TeachplanMediaPub getMedia(@PathVariable("teachplanId") String teachplanId);
}

