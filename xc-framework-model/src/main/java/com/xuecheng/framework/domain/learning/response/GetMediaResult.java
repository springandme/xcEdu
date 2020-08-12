package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @ClassName GetMediaResult
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/12 15:54
 * @Version V1.0
 **/
@Data
@ToString
@NoArgsConstructor
public class GetMediaResult extends ResponseResult {
    // 媒资文件播放地址
    private String fileUrl;

    public GetMediaResult(ResultCode resultCode, String fileUrl) {
        super(resultCode);
        this.fileUrl = fileUrl;
    }
}
