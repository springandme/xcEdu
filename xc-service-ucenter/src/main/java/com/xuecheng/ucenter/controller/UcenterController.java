package com.xuecheng.ucenter.controller;

import com.xuecheng.api.ucenter.UcenterControllerApi;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.service.UcenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UcenterController
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/14 15:55
 * @Version V1.0
 **/
@RestController
@RequestMapping("/ucenter")
public class UcenterController implements UcenterControllerApi {

    @Autowired
    private UcenterService ucenterService;


    @Override
    @GetMapping("/getuserext")
    public XcUserExt getUserExt(String username) {
        return ucenterService.getUserExt(username);
    }
}
