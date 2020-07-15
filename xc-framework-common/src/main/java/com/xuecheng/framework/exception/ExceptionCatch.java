package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @ClassName ExceptionCatch
 * @Description 统一异常捕获类
 * @Author liushi
 * @Date 2020/7/15 21:56
 * @Version V1.0
 **/
//控制器增强
@ControllerAdvice
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //捕获CustomException此类异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody  //将java对象错误信息转换为json
    public ResponseResult customException(CustomException customException) {
        //记录日志
        LOGGER.error("catch exception:{}", customException.getMessage());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }
}
