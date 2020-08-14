package com.xuecheng.auth.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName UserClient
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/14 16:33
 * @Version V1.0
 **/
@FeignClient(value = XcServiceList.XC_SERVICE_UCENTER)
public interface UserClient {

    // 根据账号查询用户信息
    @GetMapping("/ucenter/getuserext")
    XcUserExt getUserExt(@RequestParam("username") String username);

}
