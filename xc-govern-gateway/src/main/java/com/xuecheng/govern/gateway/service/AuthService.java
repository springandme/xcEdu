package com.xuecheng.govern.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @ClassName AuthService
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/14 21:55
 * @Version V1.0
 **/
@Service
public class AuthService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // 从头信息取出jwt令牌


    // 从cookie取出token

    // 从Redis取出jwt令牌

    // 拒绝访问
}
