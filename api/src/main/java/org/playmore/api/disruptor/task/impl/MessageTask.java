package org.playmore.api.disruptor.task.impl;

import org.playmore.api.disruptor.TaskDisruptor;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.common.util.LogUtil;

/**
 * @ClassName MessageTask
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:45
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:45
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class MessageTask implements Runnable {

    private final TaskDisruptor disruptor;
    private final BaseTask task;

    public MessageTask(TaskDisruptor disruptor, BaseTask task) {
        this.disruptor = disruptor;
        this.task = task;
    }

    @Override
    public void run() {
        disruptor.publish(task);
        LogUtil.message("traceId: ", task.getTraceId(), "req: ", task.getReq(), ", task", task.getClass(), ", 抛入消息环成功!");
    }
}
