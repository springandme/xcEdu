package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AuthService
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/14 10:54
 * @Version V1.0
 **/
@Service
public class AuthService {

    // Eureka负载均衡客户端
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    // java操作Redis的模板
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // token存储到redis的过期时间,单位是秒
    @Value("${auth.tokenValiditySeconds}")
    private long tokenValiditySeconds;

    /**
     * 用户认证申请令牌,将令牌存储到Redis
     *
     * @param username     用户账号
     * @param password     用户密码
     * @param clientId     客户端id
     * @param clientSecret 客户端凭证
     * @return AuthToken
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        // 远程请求spring security 申请令牌
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        // 用户身份令牌
        String access_token = authToken.getAccess_token();
        // 内容 内容就是AuthToken对象的内容
        String content = JSON.toJSONString(authToken);
        // 将令牌存储到Redis
        boolean bool = this.saveToken(access_token, content, tokenValiditySeconds);
        if (!bool) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_SAVETOKEN_FAIL);
        }
        return authToken;
    }

    /**
     * 申请令牌
     *
     * @param username     用户账号
     * @param password     用户密码
     * @param clientId     客户端id
     * @param clientSecret 客户端凭证
     * @return AuthToken
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        // 从Eureka中获取认证服务地址(因为spring security在认证服务中)
        // 从Eureka中获取认证服务的一个实例地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        // 此地址就是http://ip:port
        URI uri = serviceInstance.getUri();

        // 令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";
        System.out.println(authUrl);

        // 定义header--使用LinkedMultiValueMap储存多个header信息
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = this.getHttpBasic(clientId, clientSecret);
        header.add("Authorization", httpBasic);

        // 定义body  --设置请求中的body信息
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);

        // 设置restTemplate远程调用时候,对400和401不让报错,返回正常数据
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // 当响应的值为400或者401时也要正常响应,不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });

        // 远程调用令牌
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

        // 申请令牌内容
        Map responseBody = exchange.getBody();
        // 校验获取到的jwt是否完成
        if (responseBody == null ||
                responseBody.get("access_token") == null ||
                responseBody.get("refresh_token") == null ||
                responseBody.get("jti") == null) {
            return null;
        }

        // 拼装AuthToken并返回
        AuthToken authToken = new AuthToken();
        // 用户身份令牌
        authToken.setAccess_token((String) responseBody.get("jti"));
        // 刷新令牌
        authToken.setJwt_token((String) responseBody.get("access_token"));
        // jwt令牌
        authToken.setRefresh_token((String) responseBody.get("refresh_token"));

        return authToken;
    }


    /**
     * 存储令牌到Redis
     *
     * @param access_token 用户身份令牌
     * @param content      内容就是AuthToken对象的内容
     * @param ttl          在Redis中过期时间
     * @return true 表示expire > 0
     */
    private boolean saveToken(String access_token, String content, long ttl) {
        String key = "user_token: " + access_token;
        // 保存令牌到Redis
        stringRedisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        // 获取过期时间
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        // 大于0则返回true
        return expire > 0;
    }

    /**
     * 获取HTTPBasic串
     *
     * @param clientId     客户端id
     * @param clientSecret 客户端凭证
     * @return HTTPBasic串
     */
    private String getHttpBasic(String clientId, String clientSecret) {
        // 将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId + ":" + clientSecret;
        // 进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }
}
