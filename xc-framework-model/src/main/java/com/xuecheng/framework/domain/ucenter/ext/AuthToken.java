package com.xuecheng.framework.domain.ucenter.ext;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by mrt on 2018/5/21.
 */
@Data
@ToString
@NoArgsConstructor
public class AuthToken {
    String access_token;//访问token ,就是那个短令牌,用户身份令牌 "jti": "a8c4aecb-f7b2-40b8-b0d5-5553f5bb98dd"
    String refresh_token;//刷新token  refresh_token
    String jwt_token;//jwt令牌  access_token  长令牌
}
