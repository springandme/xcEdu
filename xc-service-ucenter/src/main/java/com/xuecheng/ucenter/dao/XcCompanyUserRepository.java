package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser, String> {
    // 根据用户的id去查询用户所属的公司id
    XcCompanyUser findByUserId(String userId);
}
