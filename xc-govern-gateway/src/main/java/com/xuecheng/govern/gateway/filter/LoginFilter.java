package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName LoginFilterTest
 * @Description 身份校验过滤器
 * @Author liushi
 * @Date 2020/8/14 21:19
 * @Version V1.0
 **/
@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    private AuthService authService;

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
     * 过滤器的业务逻辑-->实现网关连接Redis校验令牌
     * -
     * 从cookie查询用户身份令牌是否存在,不存在则拒绝访问
     * 从http header查询jwt令牌是否存在,不存在则拒绝访问
     * 从Redis查询user_token令牌是否过期,过期则拒绝访问
     *
     * @return response
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();
        // 取出cookie中的身份令牌
        String access_token = authService.getTokenFromCookie(request);
        if (StringUtils.isEmpty(access_token)) {
            // 拒绝访问
            this.access_denied();
            return null;
        }
        // 从header中取jwt
        String jwt = authService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(jwt)) {
            // 拒绝访问
            this.access_denied();
            return null;
        }
        // 从Redis取出jwt的过期时间
        long expire = authService.getJwtFromRedis(access_token);
        if (expire < 0) {
            // 拒绝访问
            this.access_denied();
            return null;
        }
        return null;
    }


    /**
     * 拒绝访问
     */
    private void access_denied() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        // 拒绝访问
        requestContext.setSendZuulResponse(false);
        // 设置响应代码
        requestContext.setResponseStatusCode(200);
        // 构建响应的信息
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        // 转成JSON
        String toJSONString = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(toJSONString);
        // 设置返回类型为application/json
        requestContext.getResponse().setContentType("application/json;charset=utf-8");
    }

}
