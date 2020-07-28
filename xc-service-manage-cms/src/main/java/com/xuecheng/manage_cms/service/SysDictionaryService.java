package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName SysDictionaryService
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/28 16:09
 * @Version V1.0
 **/
@Service
public class SysDictionaryService {

    @Autowired
    private SysDictionaryRepository sysDictionaryRepository;

    /**
     * 根基字典分类dType 查询字典信息
     *
     * @param type 字典分类dType
     * @return SysDictionary
     */
    public SysDictionary getDictionaryByType(String type) {
        return sysDictionaryRepository.findByDType(type);
    }
}
