package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName CategoryService
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/28 15:52
 * @Version V1.0
 **/
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    /**
     * 课程分类查询
     *
     * @return CategoryNode
     */
    public CategoryNode findList() {
        return categoryMapper.selectList();
    }


}
