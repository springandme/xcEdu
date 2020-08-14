package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName AuthControllerApi
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/13 21:02
 * @Version V1.0
 **/
@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {

    // 客户端认证信息
    // 客户端id
    @Value("${auth.clientId}")
    private String clientId;
    // 客户端凭证
    @Value("${auth.clientSecret}")
    private String clientSecret;

    // cookie域
    @Value("${auth.cookieDomain}")
    private String cookieDomain;
    // cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    //  token存储到redis的过期时间,单位是秒
    @Value("${auth.tokenValiditySeconds}")
    private int tokenValiditySeconds;

    @Autowired
    private AuthService authService;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        if (StringUtils.isEmpty(loginRequest.getPassword())) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        // 用户账户
        String username = loginRequest.getUsername();
        // 用户密码
        String password = loginRequest.getPassword();

        // 申请令牌
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);
        // 用户身份令牌 jti 短令牌 ,
        String access_token = authToken.getAccess_token();
        // 将令牌存储到cookie
        this.saveCookie(access_token);

        return new LoginResult(CommonCode.SUCCESS, access_token);
    }


    /**
     * 存储cookie
     *
     * @param token jwt令牌
     */
    private void saveCookie(String token) {

        HttpServletResponse response =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        // httpOnly false -->浏览器可以获取cookie
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);
    }


    @Override
    public ResponseResult logout() {
        return null;
    }
}
