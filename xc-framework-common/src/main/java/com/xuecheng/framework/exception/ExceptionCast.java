package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @ClassName ExceptionCast
 * @Description 异常抛出类  ==> 封装throw new CustomException()
 * @Author liushi
 * @Date 2020/7/15 21:53
 * @Version V1.0
 **/
public class ExceptionCast {

    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }
}
