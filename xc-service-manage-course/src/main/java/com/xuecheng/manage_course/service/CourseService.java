package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Autowired
    private TeachPlanMediaRepository teachPlanMediaRepository;

    @Value("${course‐publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course‐publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course‐publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course‐publish.siteId}")
    private String publish_siteId;
    @Value("${course‐publish.templateId}")
    private String publish_templateId;
    @Value("${course‐publish.previewUrl}")
    private String previewUrl;


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


    /**
     * 向课程管理数据添加课程图片的关联信息
     *
     * @param courseId 课程id
     * @param pic      图片id
     * @return ResponseResult
     */
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程图片信息
        CoursePic coursePic = null;
        //先查询图片
        Optional<CoursePic> one = coursePicRepository.findById(courseId);
        if (one.isPresent()) {
            coursePic = one.get();
        }
        if (coursePic == null) {
            coursePic = new CoursePic();
        }

        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);

        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 根据课程id查询课程的图片信息
     *
     * @param courseId 课程id
     * @return CoursePic
     */
    public CoursePic getCoursePic(String courseId) {
        Optional<CoursePic> one = coursePicRepository.findById(courseId);
        if (one.isPresent()) {
            return one.get();
        }
        return null;
    }


    /**
     * 根据课程id去删除课程图片信息
     *
     * @param courseId 课程id
     * @return ResponseResult
     */
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        //执行删除
        long result = coursePicRepository.deleteByCourseid(courseId);
        if (result > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    /**
     * 查询课程视图,包括基本信息,图片,营销,课程计划,这些就是课程详情预览的数据模型dataUrl
     *
     * @param id 课程id
     * @return CourseView
     */
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();

        //查询课程的基本信息
        Optional<CourseBase> optionalCourseBase = courseBaseRepository.findById(id);
        if (optionalCourseBase.isPresent()) {
            courseView.setCourseBase(optionalCourseBase.get());
        }
        //查询课程图片
        Optional<CoursePic> optionalCoursePic = coursePicRepository.findById(id);
        if (optionalCoursePic.isPresent()) {
            courseView.setCoursePic(optionalCoursePic.get());
        }
        //查询课程营销
        Optional<CourseMarket> optionalCourseMarket = courseMarketRepository.findById(id);
        if (optionalCourseMarket.isPresent()) {
            courseView.setCourseMarket(optionalCourseMarket.get());
        }
        //查询课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }


    /**
     * 根据id查询课程基本信息
     *
     * @param courseId 课程id
     * @return CourseBase
     */
    public CourseBase findCourseBaseById(String courseId) {
        //获取课程基本信息
        Optional<CourseBase> one = courseBaseRepository.findById(courseId);
        if (one.isPresent()) {
            return one.get();
        }
        //课程基本不存在,则抛出异常
        ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEBASEISNULL);
        return null;
    }


    /**
     * 课程预览,添加课程详细页面,远程调用cms服务添加页面,最终按返回课程预览url
     *
     * @param courseId 课程id
     * @return CoursePublishResult
     */
    public CoursePublishResult coursePreview(String courseId) {
        //请求cms添加页面

        //获取课程信息
        CourseBase courseBase = this.findCourseBaseById(courseId);

        //准备CMSPage信息
        CmsPage cmsPage = new CmsPage();
        //数据模型url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        //页面名称
        cmsPage.setPageName(courseId + ".html");
        //页面物理路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //页面webPath
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面站点
        cmsPage.setSiteId(publish_siteId);
        //页面模板
        cmsPage.setTemplateId(publish_templateId);
        //页面别名,就是课程名称
        cmsPage.setPageAliase(courseBase.getName());

        //远程调用,保存页面信息
        CmsPageResult result = cmsPageClient.saveCmsPage(cmsPage);
        if (!result.isSuccess()) {
            //返回失败
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        //页面id
        String cmsPageId = result.getCmsPage().getPageId();
        //拼装页面预览的url
        String url = previewUrl + cmsPageId;
        //返回CoursePublishResult对象,(当中包括了页面预览的url)
        return new CoursePublishResult(CommonCode.SUCCESS, url);
    }


    /**
     * 课程发布
     *
     * @param courseId 课程id
     * @return CoursePublishResult
     */
    @Transactional
    public CoursePublishResult coursePublish(String courseId) {
        //获取课程信息
        CourseBase courseBase = this.findCourseBaseById(courseId);

        //准备CMSPage信息
        CmsPage cmsPage = new CmsPage();
        //数据模型url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        //页面名称
        cmsPage.setPageName(courseId + ".html");
        //页面物理路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //页面webPath
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面站点
        cmsPage.setSiteId(publish_siteId);
        //页面模板
        cmsPage.setTemplateId(publish_templateId);
        //页面别名,就是课程名称
        cmsPage.setPageAliase(courseBase.getName());

        //调用cms一键发布接口将页面详情发布到服务器
        CmsPostPageResult postPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!postPageResult.isSuccess()) {
            //返回失败
            return new CoursePublishResult(CommonCode.FAIL, null);
        }

        //保存课程的发布状态为"已发布",修改mysql表中信息,需要加上事务注解@Transactional
        CourseBase saveCoursePubStats = this.saveCoursePubStats(courseId);
        if (saveCoursePubStats == null) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }

        //保存课程索引信息

        //缓存课程信息

        //得到页面的url
        String pageUrl = postPageResult.getPageUrl();

        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    //

    /**
     * 更改基本课程状态,为已发布,数据字典状态码,202002
     *
     * @param courseId 课程id
     * @return CourseBase
     */
    private CourseBase saveCoursePubStats(String courseId) {
        CourseBase courseBaseById = this.findCourseBaseById(courseId);
        courseBaseById.setStatus("202002");

        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }

    /**
     * 保存媒资信息
     *
     * @param teachplanMedia 课程计划与媒资关联信息
     * @return ResponseResult
     */
    @Transactional
    //保存课程计划与媒资文件的关联信息
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //校验课程计划是否是3级
        //课程计划
        String teachplanId = teachplanMedia.getTeachplanId();
        //查询到课程计划
        Optional<Teachplan> optional = teachPlanRepository.findById(teachplanId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //查询到教学计划
        Teachplan teachplan = optional.get();
        //取出等级
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade) || !grade.equals("3")) {
            //只允许选择第三级的课程计划关联视频
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        //查询teachplanMedia
        Optional<TeachplanMedia> mediaOptional = teachPlanMediaRepository.findById(teachplanId);
        TeachplanMedia one = null;
        if (mediaOptional.isPresent()) {
            one = mediaOptional.get();
        } else {
            one = new TeachplanMedia();
        }

        //将one保存到数据库
        one.setCourseId(teachplan.getCourseid());//课程id
        one.setMediaId(teachplanMedia.getMediaId());//媒资文件的id
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());//媒资文件的原始名称
        one.setMediaUrl(teachplanMedia.getMediaUrl());//媒资文件的url
        one.setTeachplanId(teachplanId);
        teachPlanMediaRepository.save(one);

        return new ResponseResult(CommonCode.SUCCESS);
    }
}
