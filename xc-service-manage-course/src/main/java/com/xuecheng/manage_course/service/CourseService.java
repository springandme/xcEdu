package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.TeachPlanRepository;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @ClassName CourseService
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/27 16:18
 * @Version V1.0
 **/
@Service
public class CourseService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachPlanRepository teachPlanRepository;

    @Autowired
    private CourseBaseRepository courseBaseRepository;


    /**
     * 查询课程计划
     *
     * @param courseId 课程id
     * @return TeachplanNode
     */
    public TeachplanNode findTeachPlanList(String courseId) {
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        if (teachplanNode != null) {
            return teachplanNode;
        }
        return null;
    }


    /**
     * 添加课程计划
     *
     * @param teachplan 课程信息
     * @return ResponseResult
     */
    //mysql数据库增删改,一定要加事务管理,MongoDB不支持事务
    @Transactional
    public ResponseResult addTeachPlan(Teachplan teachplan) {
        //前端数据校验设置 章节/课时名称pname是必须填的 还有状态也是必填的
        if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程id
        String courseId = teachplan.getCourseid();
        //页面传入的parentId
        String parentId = teachplan.getParentid();
        if (StringUtils.isEmpty(parentId)) {
            //取出该课程的根节点
            parentId = this.getTeachplanRoot(courseId);
        }
        Optional<Teachplan> optional = teachPlanRepository.findById(parentId);
        Teachplan parentNode = optional.get();
        //父节点的级别
        String grade = parentNode.getGrade();
        //新节点
        Teachplan teachplanNew = new Teachplan();
        //将页面提交的teachplan信息拷贝到teachplanNew对象中
        BeanUtils.copyProperties(teachplan, teachplanNew);
        teachplanNew.setParentid(parentId);
        teachplanNew.setCourseid(courseId);
        //设置级别,根据父节点的级别来设置 父是0 ,该级别就是1
        if ("1".equals(grade)) {
            teachplanNew.setGrade("2");
        } else {
            teachplanNew.setGrade("3");
        }

        teachPlanRepository.save(teachplanNew);

        //要处理parentId

        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 查询课程的根节点,如果查询不到要自己添加根节点
     *
     * @param courseId 课程id
     * @return 根节点id
     */
    private String getTeachplanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        //课程信息
        CourseBase courseBase = optional.get();
        //  查询课程的根节点
        List<Teachplan> teachplanList = teachPlanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() <= 0) {
            //查询不到,要自动添加根节点
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            teachPlanRepository.save(teachplan);
            return teachplan.getId();
        }
        //返回根节点id
        return teachplanList.get(0).getId();
    }
}
