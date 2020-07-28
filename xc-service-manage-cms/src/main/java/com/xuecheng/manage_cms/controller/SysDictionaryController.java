package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.SysDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SysDictionaryController
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/28 16:15
 * @Version V1.0
 **/
@RestController
@RequestMapping("/sys/dictionary")
public class SysDictionaryController implements SysDictionaryControllerApi {

    @Autowired
    private SysDictionaryService sysDictionaryService;

    @Override
    @GetMapping("/get/{type}")
    public SysDictionary getByType(@PathVariable("type") String type) {
        return sysDictionaryService.getDictionaryByType(type);
    }
}
