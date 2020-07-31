package com.xuecheng.test.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @ClassName TestFastDFS
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/29 17:26
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {


    //上传文件测试
    @Test
    public void testUpdate() {
        try {
            //加载fastdfs-clint.properties
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient 用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接Tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storeStorage);

            //向storage服务器上传文件
            //本地文件的路径  "C:/Users/1111/Desktop/人工智能页面插图/第一章/3.jpg"
            //"C:/Users/1111/Desktop/人工智能页面插图/第一章/Postage.png"
            //"F:/teach/xcEdu/xcEduUI0001/xc-ui-pc-static-portal/img/widget-bannerBg.png"
            // group1/M00/00/00/rBqoQF8jz8CANMPdAAKEugxHu7A371.png
            // "F:\\teach\\xcEdu\\xcEduUI0001\\xc-ui-pc-static-portal\\img\\widget-myImg.jpg";
            // group1/M00/00/00/rBqoQF8j1jOAXxvYAAAWh_Cdpwo511.jpg
            String filePath = "F:\\teach\\xcEdu\\xcEduUI0001\\xc-ui-pc-static-portal\\img\\widget-myImg.jpg";
            //上传成功后拿到文件id
            String upload_file1Id = storageClient1.upload_file1(filePath, "jpg", null);
            //group1/M00/00/00/rBqoQF8hR_mAKL76AADWY6U4-pM107.jpg
            //group1/M00/00/00/rBqoQF8hdpuAKxfWAARZMK5dcoA647.png
            System.out.println(upload_file1Id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //下载文件测试
    @Test
    public void testDownload() {
        try {
            //加载fastdfs-clint.properties
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient 用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接Tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storeStorage);

            String fileId = "group1/M00/00/00/rBqoQF8hR_mAKL76AADWY6U4-pM107.jpg";
            //下载文件
            byte[] bytes = storageClient1.download_file1(fileId);
            //使用输出流保存文件
            FileOutputStream fileOutputStream = new FileOutputStream(new File("E:/Data/test.jpg"));
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
