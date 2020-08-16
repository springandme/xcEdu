package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface XcMenuMapper {

    /**
     * 根据用户id查询用户的权限
     *
     * @param userId 用户id
     * @return List<XcMenu>
     */
    List<XcMenu> selectPermissionByUserId(String userId);
}
