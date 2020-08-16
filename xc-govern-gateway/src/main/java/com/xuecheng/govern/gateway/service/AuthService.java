package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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


    /**
     * 从头信息取出jwt令牌
     *
     * @param request 请求
     * @return jwt令牌
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        // 取出头信息
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            // 拒接访问
            return null;
        }
        if (!authorization.startsWith("Bearer ")) {
            // 拒绝访问
            return null;
        }
        String jwt = authorization.substring(7);
        return jwt;
    }


    /**
     * 取出cookie中的用户身份令牌
     *
     * @return cookie中的用户身份令牌
     */
    public String getTokenFromCookie(HttpServletRequest request) {
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if (map != null && map.get("uid") != null) {
            return map.get("uid");
        }
        return null;
    }


    /**
     * Redis查询令牌的有效期
     *
     * @return jwt令牌
     */
    public long getJwtFromRedis(String access_token) {
        String key = "user_token: " + access_token;
        // 从Redis中取到令牌信息
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire;
    }

}
