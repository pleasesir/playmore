package org.playmore.api.disruptor.handler;

import com.lmax.disruptor.WorkHandler;
import lombok.Getter;
import org.playmore.api.disruptor.TaskDisruptor;
import org.playmore.api.disruptor.queue.ConcurrentArrayQueue;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.common.util.ExceptionUtil;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.NumberUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName TaskWorkHandler
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:48
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:48
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class TaskWorkHandler implements WorkHandler<TaskEventBuffer<BaseTask>> {

    private static final String ASYNC = "async";
    private final AtomicBoolean stopping;
    private final TaskDisruptor taskDisruptor;
    @Getter
    private TaskEventBuffer<BaseTask> currentEvent;
    @Getter
    private Thread currentThread;

    public TaskWorkHandler(AtomicBoolean stopping, TaskDisruptor disruptor) {
        this.stopping = stopping;
        taskDisruptor = disruptor;
    }

    @Override
    public void onEvent(TaskEventBuffer<BaseTask> event) throws Exception {
        try {
            if (stopping.get()) {
                LogUtil.warn("当前服务未初始化完成或服务正在停止, service: ", event.req());
                return;
            }
            currentEvent = event;
            currentThread = Thread.currentThread();
            event.execute();
            long duration;
            if ((duration = System.currentTimeMillis() - event.startTime()) >= NumberUtil.HALF_OF_HUNDRED) {
                LogUtil.warn("taskUnit take more than 50ms, spend times:", duration, "ms, execute service:", event.req(),
                        ", traceId:", event.traceId());
            }
        } catch (Throwable t) {
            ExceptionUtil.analysisThrowable(event.task().getClass(), t);
        } finally {
            // 将下一个任务放入消息环中
            ConcurrentArrayQueue<BaseTask> queue = (ConcurrentArrayQueue<BaseTask>) event.task().queue();
            if (Objects.nonNull(queue)) {
                // 移除当前, 获取下一个; 为了保证当前任务执行完后再移除当前任务, 不能先移除
                // 原因: 先移除会出现当前任务执行的线程与刚抛入的任务所执行的线程是两个不同的线程
                BaseTask nextTask = queue.pollAndPeek();

                if (Objects.nonNull(nextTask)) {
                    taskDisruptor.tryPublish(nextTask);
                }
            }

            currentEvent = null;
            currentThread = null;
            event.clear();
        }
    }
}
