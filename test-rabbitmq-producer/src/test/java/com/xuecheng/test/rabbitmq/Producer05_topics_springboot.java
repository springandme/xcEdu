package com.xuecheng.test.rabbitmq;

import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName Producer05_topics_springboot
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/25 19:36
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer05_topics_springboot {

    @Autowired
    RabbitTemplate rabbitTemplate;

    //准备使用rabbitTemplate发送消息
    @Test
    public void testSendEmail() {
        String message = "send email message to user 2020年7月25日20:27:53";
        /*
            参数:
                1.交换机名称
                2.routingKey
                3.消息内容
         */
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM, "inform.email", message);
    }
}
