package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UcenterService
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/14 15:58
 * @Version V1.0
 **/
@Service
public class UcenterService {

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    private XcUserRepository xcUserRepository;

    //

    /**
     * 根据账号去查询用户信息
     *
     * @param username 用户账号
     * @return XcUserExt
     */
    public XcUserExt getUserExt(String username) {
        // 根据账号查询xcUser信息
        XcUser xcUser = this.findXcUserByUsername(username);
        if (xcUser == null) {
            return null;
        }
        // 根据用户id查询用户所属公司id
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(xcUser.getId());
        // 取到用户的公司id
        String companyId = null;
        if (xcCompanyUser != null) {
            companyId = xcCompanyUser.getCompanyId();
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        xcUserExt.setCompanyId(companyId);
        return xcUserExt;
    }


    /**
     * 根据账号查询xcUser信息
     *
     * @param username 用户信息
     * @return XcUser
     */
    public XcUser findXcUserByUsername(String username) {
        return xcUserRepository.findByUsername(username);
    }
}
