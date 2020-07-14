package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @ClassName CmsPageRepositoryTest
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/5 12:55
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll() {
        List<CmsPage> list = cmsPageRepository.findAll();
        list.forEach(System.out::println);
    }

    //分页查询
    @Test
    public void testFindPage() {
        //分页参数
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    //添加
    @Test
    public void testInsert() {
    }

    //修改
    @Test
    public void testUpdate() {
        //查询对象
        Optional<CmsPage> optional = cmsPageRepository.findById("5a92141cb00ffc5a448ff1a0");
        if (optional.isPresent()) {                            //5a92141cb00ffc5a448ff1a0
            CmsPage cmsPage = optional.get();
            //设置修改值
            System.out.println(cmsPage);
            cmsPage.setPageAliase("test01");
            //修改
            cmsPageRepository.save(cmsPage);
        }
    }

    @Test
    public void testFindByPageName() {
        CmsPage page = cmsPageRepository.findByPageName("10101.html");
        System.out.println(page);
    }

    //自定义条件查询测试
    @Test
    public void testFIndAllByExample() {
        //分页参数
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        //T probe -->   条件值对象
        CmsPage cmsPage = new CmsPage();
        //要查询5a92141cb00ffc5a448ff1a0站点的页面
//        cmsPage.setPageId("5a92141cb00ffc5a448ff1a0");
        //设置模板id条件
//        cmsPage.setTemplateId("5a925be7b00ffc4b3c1578b5");

        //设置页面别名查询
        cmsPage.setPageAliase("轮播");
        /*
        //ExampleMatcher matcher --> 条件匹配器  无参matching()方法,为精准匹配
        ExampleMatcher matching = ExampleMatcher.matching();
        //ExampleMatcher.GenericPropertyMatchers.contains()     包换关键字
        //startWith()     前缀匹配
        matching = matching.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        */

        //链式简介写法
        ExampleMatcher matching = ExampleMatcher.matching().withMatcher("pageAliase",
                ExampleMatcher.GenericPropertyMatchers.contains());
        //定义Example
        Example<CmsPage> example = Example.of(cmsPage, matching);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);

    }
}
