package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @ClassName FileSystemService
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/29 20:18
 * @Version V1.0
 **/
@Service
public class FileSystemService {

    //把application.yml配置文件注入,使用@Value注解
    @Value("${xuecheng.fastdfs.tracker_servers}")
    public String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    public int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    public int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    public String charset;

    @Autowired
    private FileSystemRepository fileSystemRepository;

    /**
     * 上传文件,没有去判断FastDFS中是否已经存在相同的图片
     *
     * @param multipartFile 文件本身
     * @param fileTage      业务标签
     * @param businessKey   业务key
     * @param metaData      文件元信息
     * @return UploadFileResult
     */
    public UploadFileResult uploadFile(MultipartFile multipartFile, String fileTage, String businessKey,
                                       String metaData) {
        //验证提交的文件是否为空
        if (multipartFile == null) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //第一步:将文件上传到FastDFS中,得到一个id
        String fileId = this.fastDFSUpdate(multipartFile);
        if (StringUtils.isEmpty(fileId)) {
            //上传文件失败时,抛出异常
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }

        //第二步:将文件id其他文件信息存储到mongodb中

        //设置相关的文件信息
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        //FastDFS的fileId就是实际的物理路径
        fileSystem.setFilePath(fileId);
        fileSystem.setFiletag(fileTage);
        fileSystem.setBusinesskey(businessKey);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());
        //文件元数据需要转换为map对象
        if (StringUtils.isNotEmpty(metaData)) {
            try {
                Map map = JSON.parseObject(metaData, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS, fileSystem);
    }


    /**
     * 将文件上传到FastDFS中,得到一个id
     *
     * @param multipartFile 文件
     * @return 文件id
     */
    private String fastDFSUpdate(MultipartFile multipartFile) {

        try {
            //初始化环境
            this.initFastDFSConfig2();

            //创建一个TrackerClient
            TrackerClient trackerClient = new TrackerClient();
            //得到storage连接信息
            TrackerServer trackerServer = trackerClient.getConnection();
            //得到storage服务器
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient来上传文件
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storeStorage);

            //上传文件
            //得到文件的字节
            byte[] bytes = multipartFile.getBytes();
            //得到文件的原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            //得到扩展名
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

            return storageClient1.upload_file1(bytes, ext, null);
        } catch (Exception e) {
            e.printStackTrace();
            //初始化环境出错,抛出异常
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
        return null;
    }

    /**
     * 初始化FastDFS环境,bug日志,使用该方法初始化FastDFS环境,trackerClient.getConnection()返回的结果为空null!
     */
    public void initFastDFSConfig() {
        try {
            //初始化tracker服务地址(多个tracker中间以半角逗号分隔)
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
        } catch (Exception e) {
            e.printStackTrace();
            //初始化环境出错,抛出异常
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }

    /**
     * 初始化FastDFS环境
     */
    public void initFastDFSConfig2() {
        try {
            //初始化tracker服务地址(多个tracker中间以半角逗号分隔)
            //加载fastdfs-clint.properties
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
        } catch (Exception e) {
            e.printStackTrace();
            //初始化环境出错,抛出异常
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
}
