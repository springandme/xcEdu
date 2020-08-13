package com.xuecheng.manage_course.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @ClassName ResourceServerConfig
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/13 15:51
 * @Version V1.0
 **/
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)//激活方法上的PreAuthorize注解
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Value("${oauth2.urlMatchers}")
    String urlMatchers;

    //公钥
    private static final String PUBLIC_KEY = "publickey.txt";

    //定义JwtTokenStore，使用jwt令牌
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    //定义JJwtAccessTokenConverter，使用jwt令牌
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(getPubKey());
        return converter;
    }

    /**
     * 获取非对称加密公钥 Key
     *
     * @return 公钥 Key
     */
    private String getPubKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new
                    InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }
    }

    //Http安全配置，对每个到达系统的http请求链接进行校验
    @Override
    public void configure(HttpSecurity http) throws Exception {

        if ("".equals(urlMatchers)) {
            // 如果URLMatchers未指定,则所有url需要授权后才能被访问
            http.authorizeRequests().anyRequest().authenticated();
        } else {
            // 放行urlMatchers中指定的url条目,未指定的url仍需授权才可以访问
            String[] split = urlMatchers.split(",");
            http.authorizeRequests()
                    //下边的路径放行
                    .antMatchers(split).permitAll()
                    .anyRequest().authenticated();
        }
        /*
        //所有请求必须认证通过
        http.authorizeRequests()
                //下边的路径放行   -- "/**" 开发环境下我们可与通过使用 /** 的方式来便于我们进行单元测试,生成环境一定要去掉!!!!!!!!
                .antMatchers("/v2/api-docs", "/swagger-resources/configuration/ui",
                        "/swagger-resources", "/swagger-resources/configuration/security",
                        "/swagger-ui.html", "/webjars/**", "/**").permitAll()
                .anyRequest().authenticated();*/
    }
}