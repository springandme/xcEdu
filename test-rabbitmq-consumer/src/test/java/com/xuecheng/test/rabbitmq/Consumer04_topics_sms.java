package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @ClassName Consumer02_subscribe_email
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/25 16:27
 * @Version V1.0
 **/
public class Consumer04_topics_sms {
    //队列-常量

    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";
    //inform.sms   inform.email.sms
    private static final String ROUTINGKEY_SMS = "inform.#.sms.#";


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
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            //  3.2声明一个交换换机
            /*  参数: String exchange, String type

                参数明细:
                1.交换机的名称
                2.交换机的类型
                    fanout:对应的rabbitmq的工作模式是 publish/subscribe [发布订阅模式]
                    direct:对应的Routing [路由]工作模式
                    topic:对应Topics [通配符]工作迷失
                    headers:对应的Header [Header转换器]工作模式
             */
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);
            //  3.3 进行交换机和队列绑定
            /*  参数:String queue,String exchange,String routingKey

                参数明细:
                 1.queue:队列
                 2.exchange:交换机
                 3.routingKey:路由key,作用是交换机根据路由key的值将消息转发到指定的队列中,在发布订阅模式中协助为空字符串

             */
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_TOPICS_INFORM, ROUTINGKEY_SMS);

            //实现消费方法
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
                /**
                 *当接受到消息后,此方法将被调用
                 * @param consumerTag 消费者标签,用来表示消费者的,在监听队列时设置channel,basicConsume
                 * @param envelope  信封,通过envelope获得exchange,deliveryTag,routingKey
                 * @param properties 消息属性
                 * @param body   消息内容
                 * @throws IOException 异常
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    //获得交换机
                    String exchange = envelope.getExchange();
                    //消息id,mq在channel中用来标识消息的id ,可用于确认消息已接收
                    long deliveryTag = envelope.getDeliveryTag();
                    //  5.接收消息内容
                    String message = new String(body, "utf-8");
                    System.out.println("receive message:" + message);
                }
            };

            //  4.监听队列
            //参数: String queue,boolean autoAck,Consumer callback
            /*参数明细
                1. queue 队列名称
                2. autoAck 自动回复,当消费者接收到消息后要告诉mq消息也接受,若果将参数设置为true表示会自动回复mq,如果设置为false要通过编程实现回复
                3. callback 消费方法,当消费者接受到消息要执行的方法
             */
            channel.basicConsume(QUEUE_INFORM_SMS, true, defaultConsumer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
