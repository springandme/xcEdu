package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName Producer01
 * @Description RabbitMQ入门程序
 * @Author liushi
 * @Date 2020/7/23 20:46
 * @Version V1.0
 **/
public class Producer01 {

    //队列-常量
    private static final String QUEUE = "hello world";

    public static void main(String[] args) {
        //通过链接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置主机,端口,账户和密码
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机,一个mq服务可以设置多个虚拟机,每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;
        try {
            //  1.创建新连接
            connection = connectionFactory.newConnection();
            //  2.创建会话通道 channel,生产者和mq服务之间所有通信都在Channel通道中完成
            channel = connection.createChannel();
            //  3.声明队列:如果队列在mq中没有则创建
            //参数:String queue,boolean durable,boolean exclusive,boolean,autoDelete,Map<String,Object> arguments
            /*
                参数明细
                1. queue 队列名称
                2. durable 是否持久化,如果持久化,mq重启后队列还在
                3. exclusive 是否独占连接,队列只允许在该连接中访问,如果connection连接关闭队列自动删除,
                                如果将此参数设置为true,可用于临时队列的创建
                4. autoDelete 是否自动删除 队列不再使用时是否删除此队列,如果将此参数和exclusive参数设置为true就可以实现临时队列(队列不用了就自动删除)
                5. arguments 参数 可以设置一个队列的扩展参数,比如:可设置存活时间,
             */
            channel.queueDeclare(QUEUE, true, false, false, null);
            //  4.发送消息
            //参数: String exchange,String routingKey,BasicProperties props, byte[] body
            /*  参数明细
                 1. exchange 交换机如果不指定将使用mq的默认交换机(设置为"")
                 2. routingKey :路由key 交换机根据路由key来将消息转发给指定的队列,如果使用默认交换机,routingKey设置为队列名称
                 3. props 消息的属性
                 4. body 消息内容
             */
            String message = "hello world liushi";
            channel.basicPublish("", QUEUE, null, message.getBytes());
            System.out.println("send to mq " + message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            //先关通道
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            //再关连接
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}