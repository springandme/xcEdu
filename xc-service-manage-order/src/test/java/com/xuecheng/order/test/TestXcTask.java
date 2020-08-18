package com.xuecheng.order.test;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.dao.XcTaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * @ClassName TestXcTask
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/17 19:00
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestXcTask {


    @Autowired
    private XcTaskRepository xcTaskRepository;

    @Test
    public void TestFindById() {
        Optional<XcTask> one = xcTaskRepository.findById("10");
        if (one.isPresent()) {
            XcTask xcTask = one.get();
            System.out.println(xcTask);
        }
    }
}
