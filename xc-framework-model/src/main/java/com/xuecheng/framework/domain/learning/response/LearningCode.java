package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.ToString;

/**
 * @ClassName LearningCode
 * @Description Learning-错误代码-枚举
 * @Author liushi
 * @Date 2020/8/12 16:31
 * @Version V1.0
 **/
@ToString
public enum LearningCode implements ResultCode {

    LEARNING_GET_MEDIA_ERROR(false, 23001, "学习中心获取媒资信息错误！");

    //操作代码
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

    private LearningCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public int code() {
        return 0;
    }

    @Override
    public String message() {
        return null;
    }
}
