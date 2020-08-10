package com.xuecheng.manage_media_process;

import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {

    //使用processBuilder来调用第三方应用程序
    @Test
    public void testProcessBuilder() throws IOException {

        //创建ProcessBuilder对象
        ProcessBuilder processBuilder = new ProcessBuilder();
        //设置执行的第三方程序(命令)
//        processBuilder.command("ping","127.0.0.1");
        processBuilder.command("ipconfig");
//        processBuilder.command("java","-jar","f:/xc-service-manage-course.jar");
        //将标准输入流和错误输入流合并，通过标准输入流读取信息就可以拿到第三方程序输出的错误信息、正常信息
        processBuilder.redirectErrorStream(true);

        //启动一个进程
        Process process = processBuilder.start();
        //由于前边将错误和正常信息合并在输入流，只读取输入流
        InputStream inputStream = process.getInputStream();
        //将字节流转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
        //字符缓冲区
        char[] chars = new char[1024];
        int len = -1;
        while ((len = reader.read(chars)) != -1) {
            String string = new String(chars, 0, len);
            System.out.println(string);
        }

        inputStream.close();
        reader.close();

    }


    //测试使用工具类将avi转成mp4
    @Test
    public void testProcessMp4() {
        //String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        //ffmpeg的路径
        String ffmpeg_path = "D:\\Program Files\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
        //video_path视频地址
        String video_path = "D:/FFOutput/ffmpeg_test/GIFTemp_DFC.avi";
        //mp4_name mp4文件名称
        String mp4_name = "1.mp4";
        //mp4folder_path mp4文件目录路径
        String mp4folder_path = "D:/FFOutput/ffmpeg_test/";
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4folder_path);
        //开始编码,如果成功返回success，否则返回输出的日志
        String result = mp4VideoUtil.generateMp4();
        System.out.println(result);
    }


    //使用processBuilder来调用第三方应用程序
    @Test
    public void testProcessBuilder2() throws Exception {
        //创建processBuilder对象
        ProcessBuilder processBuilder = new ProcessBuilder();
        //设置第三方应用程序
        processBuilder.command("ping", "127.0.0.1");

        //将标准输入流和错误流合并
        processBuilder.redirectErrorStream(true);
        //启动一个进程,  开始启动
        Process process = processBuilder.start();

        //通过标准输入流来拿到正常和错误的信息
        InputStream inputStream = process.getInputStream();
        //使用org.apache.commons.io.IOUtils转成字符串
        // String content = IOUtils.toString(inputStream, "gbk");
        //打印内容
        //System.out.println(content);

        //转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
        //定义缓冲区
        char[] chars = new char[1024];
        int len = -1;
        //把reader字符流内容读到 chars字符数组中
        while ((len = reader.read(chars)) != -1) {
            String string = new String(chars, 0, len);
            System.out.println(string);
        }
        //关闭流
        reader.close();
        inputStream.close();
    }

    @Test
    public void testFFmpeg() throws Exception {
        //创建processBuilder对象
        ProcessBuilder processBuilder = new ProcessBuilder();
        //设置第三方应用程序
        //定义命令内容
        List<String> command = new ArrayList<>();
        command.add("D:/soft/ffmpeg-20200315-c467328-win64-static/bin/ffmpeg.exe");
        command.add("-i");
        command.add("E:/temp/1.avi");
        command.add("-y"); //覆盖输出文件
        command.add("-c:v");
        command.add("libx264");
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add("E:/temp/1.mp4");
        processBuilder.command(command);

        //将标准输入流和错误流合并
        processBuilder.redirectErrorStream(true);
        //启动一个进程,  开始启动
        Process process = processBuilder.start();

        //通过标准输入流来拿到正常和错误的信息
        InputStream inputStream = process.getInputStream();
        //使用org.apache.commons.io.IOUtils转成字符串
        // String content = IOUtils.toString(inputStream, "gbk");
        //打印内容
        //System.out.println(content);

        //转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream, "gbk");
        //定义缓冲区
        char[] chars = new char[1024];
        int len = -1;
        //把reader字符流内容读到 chars字符数组中
        while ((len = reader.read(chars)) != -1) {
            String string = new String(chars, 0, len);
            System.out.println(string);
        }
        //关闭流
        reader.close();
        inputStream.close();
    }
}
