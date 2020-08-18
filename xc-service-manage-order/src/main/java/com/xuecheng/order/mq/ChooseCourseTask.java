package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.XcTaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @ClassName ChooseCourseTask
 * @Description Spring Task串行任务-->有顺序的依次执行
 * @Author liushi
 * @Date 2020/8/17 17:41
 * @Version V1.0
 **/
@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    private XcTaskService xcTaskService;

    //每隔1分钟扫描消息表，向mq发送消息
    @Scheduled(cron = "0/30 * * * * *")
    public void sendChooseCourseTask() {
        //取出当前时间1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        List<XcTask> taskList = xcTaskService.findTaskList(10, time);
        System.out.println(taskList);
        // 调用service发布消息,将添加选课的任务发送给mq
        for (XcTask xcTask : taskList) {
            String taskId = xcTask.getId();
            Integer version = xcTask.getVersion();
            // 通过乐观锁的方式来更新数据库,如果结果大于0说明取到任务
            if (xcTaskService.getTask(taskId, version) > 0) {
                //发送消息到MQ
                xcTaskService.publishChooseMsg(xcTask, xcTask.getMqExchange(), xcTask.getMqRoutingkey());
                LOGGER.info("send choose course task id:{}", taskId);
            }
        }
    }


    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChooseCourseTask(XcTask xcTask) {
        if (xcTask != null && StringUtils.isNotEmpty(xcTask.getId())) {
            LOGGER.info("receiveFinishChooseCourseTask...{}", xcTask.getId());
            // 接收到 的消息id
            String id = xcTask.getId();
            //删除任务，添加历史任务
            xcTaskService.finishTask(id);
        }
    }


    /**
     * 定时任务调试策略
     *
     * @Scheduled
     */
    // @Scheduled(fixedRate = 3000)  // 在任务开始后3秒执行下一次调度
    // @Scheduled(fixedDelay = 3000)  // 在任务结束后3秒执行下一次调度
    // @Scheduled(cron = "0/3 * * * * *")// 每隔3秒执行一次
    public void task1() {
        LOGGER.info("===============测试定时任务1开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("===============测试定时任务1结束===============");
    }

    // @Scheduled(cron = "0/3 * * * * *")// 每隔3秒执行一次
    public void task2() {
        LOGGER.info("===============测试定时任务2开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("===============测试定时任务2结束===============");
    }
}
