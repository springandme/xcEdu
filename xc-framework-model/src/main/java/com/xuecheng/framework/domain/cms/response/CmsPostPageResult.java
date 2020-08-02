package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @ClassName CmsPostPageResult
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/1 15:00
 * @Version V1.0
 **/
@Data
@ToString
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {

    String pageUrl;

    public CmsPostPageResult(ResultCode resultCode, String pageUrl) {
        super(resultCode);
        this.pageUrl = pageUrl;
    }
}
