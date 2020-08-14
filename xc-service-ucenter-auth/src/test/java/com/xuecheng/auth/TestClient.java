package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @ClassName
 * @Description
 * @Author liushi
 * @Date 2020/8/13 20:27
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    @Autowired
    private RestTemplate restTemplate;

    // Eureka负载均衡客户端
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    // 远程请求spring security获取令牌
    @Test
    public void testClient() {
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
        String httpBasic = this.getHttpBasic("XcWebApp", "XcWebApp");
        header.add("Authorization", httpBasic);

        // 定义body  --设置请求中的body信息
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", "itcast");
        body.add("password", "1234");
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
        System.out.println(responseBody);

    }

    // 获取HTTPBasic串
    private String getHttpBasic(String clientId, String clientSecret) {
        // 将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId + ":" + clientSecret;
        // 进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }

    @Test
    public void testPasswordEncoder() {
        String password = "111111";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        for (int i = 0; i < 10; i++) {
            // 使用BCrypt加密,每个计算出的Hash值都不一样
            String hashPass = passwordEncoder.encode(password);
            System.out.println(hashPass);
            // 虽然每次计算的密码Hash值不一样但是校验是通过的
            boolean f = passwordEncoder.matches(password, hashPass);
            System.out.println(f);
        }
    }

}
