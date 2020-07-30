package com.xuecheng.filesystem.controller;

import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName FileSystemController
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/29 20:51
 * @Version V1.0
 **/
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    private FileSystemService fileSystemService;


    @Override
    @PostMapping("/upload")
    public UploadFileResult uploadFile(@RequestParam("file") MultipartFile multipartFile,
                                       @RequestParam(value = "filetag", required = false) String filetage,
                                       @RequestParam(value = "businesskey", required = false) String businessKey,
                                       @RequestParam(value = "metedata", required = false) String metaData) {
        return fileSystemService.uploadFile(multipartFile, filetage, businessKey, metaData);
    }
}
