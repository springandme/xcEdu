package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName CourseView
 * @Description 课程预览
 * @Author liushi
 * @Date 2020/7/31 16:57
 * @Version V1.0
 **/

@Data
@NoArgsConstructor
@ToString
public class CourseView implements Serializable {
    CourseBase courseBase; //课程基本信息
    CourseMarket courseMarket; //课程营销信息
    CoursePic coursePic;  //课程图片
    TeachplanNode teachplanNode; //课程营销计划
}