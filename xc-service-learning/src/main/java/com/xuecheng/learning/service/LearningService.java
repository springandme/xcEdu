package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

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

    @Autowired
    private XcLearningCourseRepository xcLearningCourseRepository;

    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

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

    /**
     * 添加选课
     *
     * @param userId    用户id
     * @param courseId  课程id
     * @param valid     ?
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param xcTask    任务对象
     * @return ResponseResult
     */
    @Transactional
    public ResponseResult addCourse(String userId, String courseId, String valid, Date startTime, Date endTime,
                                    XcTask xcTask) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(LearningCode.LEARNING_GET_MEDIA_ERROR);
        }
        if (StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(LearningCode.CHOOSE_COURSE_TASK_IS_NUll);
        }
        if (xcTask == null && StringUtils.isEmpty(xcTask.getId())) {
            ExceptionCast.cast(LearningCode.CHOOSE_COURSE_TASK_IS_NUll);
        }
        // 先查询一下
        XcLearningCourse xcLearningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (xcLearningCourse != null) {
            // 有选课记录则更新日期,并且更新几率
            // 课程的开始时间
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        } else {
            //没有选课记录则添加
            xcLearningCourse = new XcLearningCourse();
            xcLearningCourse.setUserId(userId);
            xcLearningCourse.setCourseId(courseId);
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        }
        // 向历史任务表插入纪录
        Optional<XcTaskHis> optional = xcTaskHisRepository.findById(xcTask.getId());
        if (!optional.isPresent()) {
            // 查不到,添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

}
