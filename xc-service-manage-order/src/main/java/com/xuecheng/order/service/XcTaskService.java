package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName XcTaskService
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/17 18:30
 * @Version V1.0
 **/
@Service
public class XcTaskService {

    @Autowired
    private XcTaskRepository xcTaskRepository;

    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 查询任务列表的实现
     *
     * @param n          查询数量
     * @param updateTime 上次更新时间
     * @return List<XcTask>
     */

    public List<XcTask> findTaskList(int n, Date updateTime) {
        Pageable pageable = new PageRequest(0, n);
        Page<XcTask> byUpdateTimeBefore = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        return byUpdateTimeBefore.getContent();
    }


    /**
     * 发布消息
     *
     * @param xcTask     需要发布的任务
     * @param exchange   交换机
     * @param routingKey 路由
     */
    @Transactional
    public void publishChooseMsg(XcTask xcTask, String exchange, String routingKey) {
        Optional<XcTask> optional = xcTaskRepository.findById(xcTask.getId());
        if (optional.isPresent()) {
            // 发送消息
            rabbitTemplate.convertAndSend(exchange, routingKey, xcTask);
            // 更新任务时间
            XcTask one = optional.get();
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }


    /**
     * 获取任务
     *
     * @param id      task主键id
     * @param version 乐观锁设置的版本号
     * @return
     */
    @Transactional
    public int getTask(String id, int version) {
        // 通过乐观锁的方式来更新数据库,如果结果大于0说明取到任务
        int count = xcTaskRepository.updateTaskVersion(id, version);
        return count;
    }

    /**
     * 完成任务,删除task表中记录,并且将这条删除的记录添加到task_his中
     *
     * @param taskId task主键
     */
    @Transactional
    public void finishTask(String taskId) {
        Optional<XcTask> optional = xcTaskRepository.findById(taskId);
        if (optional.isPresent()) {
            // 当前任务
            XcTask xcTask = optional.get();
            // 历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            // 删除当前任务
            xcTaskRepository.deleteById(taskId);
            // 保存任务到task_his表中
            xcTaskHisRepository.save(xcTaskHis);
        }
    }
}
