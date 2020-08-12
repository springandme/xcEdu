package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.client.CourseSearchClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName LearningService
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/12 16:08
 * @Version V1.0
 **/
@Service
public class LearningService {

    @Autowired
    private CourseSearchClient courseSearchClient;


    /**
     * 获取课程的学习地址[视频播放地址]
     *
     * @param courseId    课程id
     * @param teachplanId 课程计划id
     * @return GetMediaResult
     */
    public GetMediaResult getMedia(String courseId, String teachplanId) {
        // 校验学生的学生权限....


        // 远程调用搜索服务查询课程计划所对应的课程媒资信息
        TeachplanMediaPub teachplanMediaPub = courseSearchClient.getMedia(teachplanId);
        if (teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMediaUrl())) {
            // 获取学习地址错误
            ExceptionCast.cast(LearningCode.LEARNING_GET_MEDIA_ERROR);
        }

        return new GetMediaResult(CommonCode.SUCCESS, teachplanMediaPub.getMediaUrl());
    }
}
