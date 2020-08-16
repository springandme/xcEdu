package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @ClassName CustomExceptionCatch
 * @Description 课程管理自定义异常类, 其中定义异常类型所对应的错误代码
 * @Author liushi
 * @Date 2020/8/15 20:40
 * @Version V1.0
 **/
// 控制器增强
@ControllerAdvice
public class CustomExceptionCatch extends ExceptionCatch {

    static {
        // 除了CustomException意外的异常类型及对应的错误代码在这里定义,如果不定义则统一返回固定的错误信息
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }
}
