package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName LoginFilterTest
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/14 21:19
 * @Version V1.0
 **/
// @Component
public class LoginFilterTest extends ZuulFilter {

    /**
     * 过滤器类型
     * pre:请求在被路由之前执行
     * -
     * routing:在路由请求是调用
     * -
     * post:在routing和error过滤器之后调用
     * -
     * error:处理请求是发生错误调用
     *
     * @return pre 请求在被路由之前执行
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤器的序号,越小越被优先执行
     *
     * @return 0
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 判断该过滤器是否需要执行,返回true表示要执行此过滤器,否则不执行
     *
     * @return true
     */
    @Override
    public boolean shouldFilter() {
        // 返回true表示要执行此过滤器
        return true;
    }

    /**
     * 过滤器的业务逻辑
     * 测试-->过滤所有请求,判断头部信息是否有Authorization,如果没有则拒绝访问,否则转发到微服务
     *
     * @return response
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        // 取出头部信息Authorization
        String authorization = request.getHeader("Authorization");
        // 判断用户的请求是否带有Authorization字段,如果没有则表示未认证用户
        if (StringUtils.isEmpty(authorization)) {
            // 拒绝访问
            requestContext.setSendZuulResponse(false);
            // 设置响应装填吗
            requestContext.setResponseStatusCode(200);
            //
            ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
            // 转成JSON
            String toJSONString = JSON.toJSONString(responseResult);
            requestContext.setResponseBody(toJSONString);
            // 设置返回类型为application/json
            requestContext.getResponse().setContentType("application/json;charset=utf-8");
            return null;
        }
        return null;
    }
}
