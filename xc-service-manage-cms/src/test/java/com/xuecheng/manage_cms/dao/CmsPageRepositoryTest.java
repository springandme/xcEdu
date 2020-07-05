package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public void testFindBypageName() {
        CmsPage page = cmsPageRepository.findByPageName("10101.html");
        System.out.println(page);
    }
}
