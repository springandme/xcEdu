package com.xuecheng.test.freemarker;

import com.xuecheng.test.freemarker.model.Student;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @ClassName FreemarkerTest
 * @Description FreemarkerTest
 * @Author liushi
 * @Date 2020/7/19 11:16
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    //测试静态化,基于ftl模板文件生成html文件
    @Test
    public void testGenerateHtml() throws IOException, TemplateException {
        //定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //定义模板 --使用test测试目录下面resources
        //先得到classpath的路径
        String classpath = this.getClass().getResource("/").getPath();
        //定义模板路径
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates"));
        //获取模板文件的内容
        Template template = configuration.getTemplate("test1.ftl");
        //定义数据模型
        Map map = this.getMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //获得静态化内容
        System.out.println(content);
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream outputStream = new FileOutputStream(new File("f:/Templates/test1.html"));
        //输出文件
        IOUtils.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }


    /**
     * 静态化测试,基于模板文件的内容(字符串)生成html文件
     * --这种方式更加灵活,模板可以来源于任何地方,而基于ftl文件方式,是放在工程目录下,有局限性
     *
     * @throws IOException       io
     * @throws TemplateException template
     */
    @Test
    public void testGenerateHtmlByString() throws IOException, TemplateException {
        //定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());

        //定义模板
        //模板内容，这里测试时使用简单的字符串作为模板
        String templateString = "" +
                "<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                " 名称：${name}\n" +
                " </body>\n" +
                "</html>";
        //使用一个模板加载器,将字符串变为模板
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template_test", templateString);
        //在配置中设置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板的内容
        Template template_test = configuration.getTemplate("template_test", "utf-8");

        //定义数据模型
        Map map = this.getMap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template_test, map);
        //获得静态化内容
        System.out.println(content);
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream outputStream = new FileOutputStream(new File("f:/Templates/test2.html"));
        //输出文件
        IOUtils.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }

    /**
     * @return java.util.Map
     * @Author liushi
     * @Description 获取数据模型
     * @Date 2020-07-19 11:31
     * @Param []
     */
    public Map getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "hliushi");
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
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向数据模型放stu数据
        map.put("stu1", stu1);
        //向数据模型放一个map数据
        map.put("stuMap", stuMap);
        return map;
    }
}
