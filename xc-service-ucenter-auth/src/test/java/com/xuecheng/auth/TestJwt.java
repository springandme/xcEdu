package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;

/**
 * @ClassName TestJwt
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/13 18:27
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {


    // 创建jwt令牌
    @Test
    public void testCreateJwt() {
        // 密钥库文件
        String keystore_location = "xc.keystore";
        // 密钥库的密码
        String keystore_password = "xuechengkeystore";

        // 密钥库文件路径
        ClassPathResource classPathResource = new ClassPathResource(keystore_location);
        // 密钥别名
        String alias = "xckey";
        // 密钥的访问密码
        String keyPassword = "xuecheng";
        // 密钥工厂
        KeyStoreKeyFactory keyFactory = new KeyStoreKeyFactory(classPathResource,
                keystore_password.toCharArray());
        // 密钥对(密钥和私钥)
        KeyPair keyPair = keyFactory.getKeyPair(alias, keyPassword.toCharArray());
        // 获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // jwt令牌内容
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", "123");
        map.put("name", "liushi");
        map.put("roles", "ro1,ro2");
        map.put("ext", "1");
        // 生成jwt令牌  内容 算法
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(privateKey));
        // 取出jwt令牌编码
        String token = jwt.getEncoded();
        // eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9
        // .eyJleHQiOiIxIiwicm9sZXMiOiJybzEscm8yIiwibmFtZSI6ImxpdXNoaSIsImlkIjoiMTIzIn0
        // .PL4RA5pEo8-AK4Lx3AWyo74OZFMbr5p_JpCtFKkayAcHbo_JhxyT-nZeMoIN_ZNjPGtr4IoznXEwIjMl-WAY0vge
        // -87ir9pXYNMfaaMnPFfXvPsLC3xS1dJweAGSVtH9H2ufCF3tgx3g_6GJOQhw86n7QBhANoRFECde6HoXXuGpdxepngtvcz-Gt
        // -F8r0Yltf4IzgJ8hoo1qho_yanvfVYZj-jyUANNvngItkS7yQ21mFd4Fqd7LFfnAysCBLoreUWffBvrxEKmuE8YXGEjFfi7
        // -4HgzzaBIHsbD25SC9FBzQ_HSIVTU2uh1A_YS-W1JZWw_c2SZapjJ_xbxOD83g
        System.out.println(token);
    }


    // 校验jwt令牌
    @Test
    public void testVerify() {
        // 公钥
        String publicKey = "-----BEGIN PUBLIC " +
                "KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R" +
                "+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD" +
                "+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        // jwt令牌
        String jwtString = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJleHQiOiIxIiwicm9sZXMiOiJybzEscm8yIiwibmFtZSI6ImxpdXNoaSIsImlkIjoiMTIzIn0" +
                ".PL4RA5pEo8-AK4Lx3AWyo74OZFMbr5p_JpCtFKkayAcHbo_JhxyT-nZeMoIN_ZNjPGtr4IoznXEwIjMl-WAY0vge" +
                "-87ir9pXYNMfaaMnPFfXvPsLC3xS1dJweAGSVtH9H2ufCF3tgx3g_6GJOQhw86n7QBhANoRFECde6HoXXuGpdxepngtvcz-Gt" +
                "-F8r0Yltf4IzgJ8hoo1qho_yanvfVYZj-jyUANNvngItkS7yQ21mFd4Fqd7LFfnAysCBLoreUWffBvrxEKmuE8YXGEjFfi7" +
                "-4HgzzaBIHsbD25SC9FBzQ_HSIVTU2uh1A_YS-W1JZWw_c2SZapjJ_xbxOD83g";
        // 校验jwt令牌
        Jwt jwt = JwtHelper.decodeAndVerify(jwtString, new RsaVerifier(publicKey));
        // 拿到jwt令牌中自定义内容
        String claims = jwt.getClaims();
        // {"ext":"1","roles":"ro1,ro2","name":"liushi","id":"123"}
        System.out.println(claims);

    }
}
