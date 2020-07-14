package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @ClassName CmsSiteRepositoryTest
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/13 12:16
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsSiteRepositoryTest {

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Test
    public void testFindAll() {
        List<CmsSite> list = cmsSiteRepository.findAll();
        list.forEach(System.out::println);
    }
}
