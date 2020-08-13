package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName TestRedis
 * @Description 向Redis存储信息
 * @Author liushi
 * @Date 2020/8/13 20:27
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //创建jwt令牌
    @Test
    public void testRedis() {
        // 定义key
        String key = "a8c4aecb-f7b2-40b8-b0d5-5553f5bb98dd";
        // 定义value
        HashMap<String, String> map = new HashMap<>();
        map.put("jwt", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU5NzM2NTc2NiwianRpIjoiYThjNGFlY2ItZjdiMi00MGI4LWIwZDUtNTU1M2Y1YmI5OGRkIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.RuWOhQfYy7xanUcpOZvhmKIML56kVKS1R49FvJ2AzMOfbwf3Ix6xcAf5jxZ6kk8vRbZaNiVdZJ3l3McEMtSER4TGv6TqRwjMFFC8YrUQCV-lMDsI9YwNvBOl_hE_Scu4oRDp-ibTJOxEx5YzgfF-9QcBqZgjxay9dwl7xR66FNh3UedKsaQ4t8LhssbnQNQC3TS_77zgPu27K9Hy0BpQR_hFBmxUyD-UGhRqEduAYcF7Rgyes0hVxxd6kA-FX-lbBcrp4HPC1eqIxmqPM6GECHy5RSGIYacZLrDWsi00l7eXuCaromUSAdvRtCBgKT8DnH7517z4S8EHObyBIZoJ5w");
        map.put("refresh_token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiJhOGM0YWVjYi1mN2IyLTQwYjgtYjBkNS01NTUzZjViYjk4ZGQiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU5NzM2NTc2NiwianRpIjoiZGE5ZjFlZmUtZDE0Mi00OWMxLTk2NGMtNWZjNDY2ZTlhNWQ2IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.bImJzA_NuOUAOve9cnhwN8nlF9FO9sm6OmLuUsoqTuqJ2ahvV32sLmFe2tH9ikc2tOsevDC9xpFSl4ZBmLa5SbdUKODe6GqE2Bc3uMbZP0LpNswmvzaK2Y732kccRGy6IEue2FBw79GfMGtGFMfXJmpOIxL4c-hQYG8rf9-lKkD0SnJbkKooxCGGySDdbSNHl8BdH6Doo4O0odQmVS2pRkmFE7NqXG4sDViXcuBYAgNqUxuv6-GqON3CxyBKGM8V13r695ne_pOCwBM68Tuc0MkkGaKHBPnbLOLQtd_aZsLCKrziixczgZaNNrp-VpFJjdUvlh9NEfDVxdaKjJSV-w");
        // map数据转出json
        String toJSONString = JSON.toJSONString(map);
        // 校验key是否存在,如果不存在则返回-2
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        // 存储数据
        stringRedisTemplate.boundValueOps(key).set(toJSONString, 120, TimeUnit.SECONDS);
        // 取出数据
        String string = stringRedisTemplate.opsForValue().get(key);
        /*{
            "refresh_token":
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9" +
                    "
                    .eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiJhOGM0YWVjYi1mN2IyLTQwYjgtYjBkNS01NTUzZjViYjk4ZGQiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU5NzM2NTc2NiwianRpIjoiZGE5ZjFlZmUtZDE0Mi00OWMxLTk2NGMtNWZjNDY2ZTlhNWQ2IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.bImJzA_NuOUAOve9cnhwN8nlF9FO9sm6OmLuUsoqTuqJ2ahvV32sLmFe2tH9ikc2tOsevDC9xpFSl4ZBmLa5SbdUKODe6GqE2Bc3uMbZP0LpNswmvzaK2Y732kccRGy6IEue2FBw79GfMGtGFMfXJmpOIxL4c-hQYG8rf9-lKkD0SnJbkKooxCGGySDdbSNHl8BdH6Doo4O0odQmVS2pRkmFE7NqXG4sDViXcuBYAgNqUxuv6-GqON3CxyBKGM8V13r695ne_pOCwBM68Tuc0MkkGaKHBPnbLOLQtd_aZsLCKrziixczgZaNNrp-VpFJjdUvlh9NEfDVxdaKjJSV-w", "jwt":
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9" +
                    "
                    .eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU5NzM2NTc2NiwianRpIjoiYThjNGFlY2ItZjdiMi00MGI4LWIwZDUtNTU1M2Y1YmI5OGRkIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.RuWOhQfYy7xanUcpOZvhmKIML56kVKS1R49FvJ2AzMOfbwf3Ix6xcAf5jxZ6kk8vRbZaNiVdZJ3l3McEMtSER4TGv6TqRwjMFFC8YrUQCV-lMDsI9YwNvBOl_hE_Scu4oRDp-ibTJOxEx5YzgfF-9QcBqZgjxay9dwl7xR66FNh3UedKsaQ4t8LhssbnQNQC3TS_77zgPu27K9Hy0BpQR_hFBmxUyD-UGhRqEduAYcF7Rgyes0hVxxd6kA-FX-lbBcrp4HPC1eqIxmqPM6GECHy5RSGIYacZLrDWsi00l7eXuCaromUSAdvRtCBgKT8DnH7517z4S8EHObyBIZoJ5w"
        }*/
        System.out.println(string);
        System.out.println(expire);
    }
}
