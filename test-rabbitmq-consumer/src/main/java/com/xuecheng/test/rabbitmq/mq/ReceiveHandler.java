package com.xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName ReceiveHandler
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/25 20:14
 * @Version V1.0
 **/
@Component
public class ReceiveHandler {

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void send_email(String msgStr, Message message, Channel channel) {
        System.out.println("receive message is " + msgStr);
    }
}
