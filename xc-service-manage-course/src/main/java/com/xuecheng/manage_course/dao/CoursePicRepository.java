package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePicRepository extends JpaRepository<CoursePic, String> {


    /**
     * 自定义方法,根据主键删除信息,并带有返回值
     *
     * @param courseId 课程图片主键id
     * @return 当返回值大于0, 表示删除成功的影响记录条数 等于或小于0表示删除失败
     */
    long deleteByCourseid(String courseId);
}
