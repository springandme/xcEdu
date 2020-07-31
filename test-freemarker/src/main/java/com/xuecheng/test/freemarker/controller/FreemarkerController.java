package com.xuecheng.test.freemarker.controller;

import com.xuecheng.test.freemarker.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-06-12 18:40
 **/
@RequestMapping("/freemarker")
@Controller //这里不要使用@RestController,要输出html页面,@RestController输出的是Json数据
public class FreemarkerController {
    @Autowired
    RestTemplate restTemplate;

    //课程详情页面测试
    @RequestMapping("/course")
    public String course(Map<String, Object> map) {
        ResponseEntity<Map> forEntity =
                restTemplate.getForEntity("http://localhost:31200/course/courseview/4028e581617f945f01617f9dabc40000"
                        , Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        System.out.println(body);
        return "course";
    }

    @RequestMapping("/banner")
    public String index_banner(Map<String, Object> map) {
        String dataUrl = "http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f";
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "index_banner";
    }

    /**
     * @return java.lang.String
     * @Author liushi
     * @Description 测试
     * @Date 2020-07-19 9:32
     * @Param [map]
     */
    @RequestMapping("/test1")
    public String freemarker(Map<String, Object> map) {
        //向数据模型放数据  [map就是freemarker模板所使用的的数据]
        map.put("name", "liushi");

        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());
        //朋友列表
        List<Student> friends = new ArrayList<>();
        friends.add(stu1);
        //给第二个学生设置朋友列表
        stu2.setFriends(friends);
        //给第二个学生设置最好朋友
        stu2.setBestFriend(stu1);
        //准备list数据
        List<Student> stuList = new ArrayList<>();
        //将学生列表放在list集合中
        stuList.add(stu1);
        stuList.add(stu2);
        //向数据模型放一个list数据
        map.put("stuList", stuList);

        //准备map数据
        Map<String, Student> stuMap = new HashMap<>();
//        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向数据模型放stu数据
        map.put("stu1", stu1);
        //向数据模型放一个map数据
        map.put("stuMap", stuMap);

        map.put("point", 102920122);
        //返回模板文件名称  [返回freemarker模板的位置,基于resources/templates路径的]
        return "test1";
    }
}
