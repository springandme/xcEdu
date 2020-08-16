package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @ClassName FeignClientInterceptor
 * @Description Feign拦截器
 * @Author liushi
 * @Date 2020/8/16 16:23
 * @Version V1.0
 **/
public class FeignClientInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            // 使用RequestContextHolder工具获取request相关变量
            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            // 取出当前请求的header,找到jwt令牌
            if (requestAttributes != null) {
                // 取出request
                HttpServletRequest request = requestAttributes.getRequest();
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        // 将jwt令牌想下传递
                        String name = headerNames.nextElement();
                        String values = request.getHeader(name);
                        if ("authorization".equals(name)) {
                            requestTemplate.header(name, values);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
