package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
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

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

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


    /**
     * 课程列表查询,并且带有分页效果
     * time:7.28没有处理条件查询,和图片查询
     *
     * @param page              页码,从1开始
     * @param size              每页显示的几率
     * @param courseListRequest 查询条件
     * @return QueryResponseResult
     */
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest) {
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        if (page <= 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseBasePage = courseMapper.findCourseList(courseListRequest);
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        //数据列表
        queryResult.setList(courseBasePage.getResult());
        //数据总记录数
        queryResult.setTotal(courseBasePage.getTotal());

        return new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
    }


    /**
     * 添加课程基本信息
     *
     * @param courseBase 基本课程信息
     * @return AddCourseResult
     */
    @Transactional
    public AddCourseResult addCourseResult(CourseBase courseBase) {
        //bug日志: 使用了StringUtils.isEmpty(courseBase.getId())进行校验,导致前台传入的数据被拦住了
        //  所以导致了使用了 接口测试工具Swagger可以传入成功,这是因为填写的数据含有id,而前台传入数据没有id,所会被拦住
        if (courseBase == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程状态默认为未发布
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, courseBase.getId());
    }


    /**
     * 获取课程基础信息
     *
     * @param courseId 课程基本信息id
     * @return CourseBase
     * @throws RuntimeException 不清楚
     */
    public CourseBase getCourseBaseById(String courseId) throws RuntimeException {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        Optional<CourseBase> one = courseBaseRepository.findById(courseId);
        if (one.isPresent()) {
            return one.get();
        }
        return null;
    }


    /**
     * 更新课程基础信息
     *
     * @param courseBase 基本课程信息
     * @return ResponseResult
     */
    @Transactional
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
        CourseBase one = this.getCourseBaseById(id);
        if (one == null) {
            //跑出异常
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEBASEISNULL);
        }
        //修改课程信息
        one.setName(courseBase.getName());
        one.setMt(courseBase.getMt());
        one.setSt(courseBase.getSt());
        one.setGrade(courseBase.getGrade());
        one.setStudymodel(courseBase.getStudymodel());
        one.setUsers(courseBase.getUsers());
        one.setDescription(courseBase.getDescription());
        courseBaseRepository.save(one);

        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 根据课程id查询课程营销信息
     *
     * @param courseId 课程id
     * @return CourseMarket
     */
    public CourseMarket getCourseMarketById(String courseId) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        Optional<CourseMarket> one = courseMarketRepository.findById(courseId);
        if (one.isPresent()) {
            return one.get();
        }
        return null;
    }


    /**
     * 更新课程营销信息
     * 先查询课程营销，如果存在则更新信息，否则添加课程营销信息的方法
     *
     * @param id           课程id
     * @param courseMarket 课程营销信息
     * @return ResponseResult
     */
    @Transactional
    public CourseMarket updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket one = this.getCourseMarketById(id);
        if (one != null) {
            //修改课程营销基本信息
            one.setCharge(courseMarket.getCharge());
            one.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
            one.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
            one.setPrice(courseMarket.getPrice());
            one.setQq(courseMarket.getQq());
            one.setValid(courseMarket.getValid());
        } else {
            //添加课程营销信息
            one = new CourseMarket();
            BeanUtils.copyProperties(courseMarket, one);
            //设置课程id
            one.setId(id);
        }
        courseMarketRepository.save(one);
        return one;
    }
}
