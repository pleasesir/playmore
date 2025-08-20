package org.playmore.api.disruptor;

import lombok.Getter;
import org.playmore.api.disruptor.queue.ConcurrentArrayQueue;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.NumberUtil;
import org.playmore.common.util.ThreadUtil;

import java.util.HashMap;
import java.util.Objects;

/**
 * @ClassName OrderedQueueDisruptor
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:54
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:54
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class OrderedQueueDisruptor {
    @Getter
    private final TaskDisruptor taskDisruptor;

    /**
     * 消息队列
     */
    private final HashMap<Long, ConcurrentArrayQueue<BaseTask>> queueMap;
    private final int calculateSize;

    public OrderedQueueDisruptor(TaskDisruptor taskDisruptor) {
        this(1 << 10, taskDisruptor);
    }

    public OrderedQueueDisruptor(int queueSize, TaskDisruptor taskDisruptor) {
        this.taskDisruptor = taskDisruptor;
        queueSize = OrderedQueueDisruptor.ceilingNextPowerOfTwo(queueSize);
        queueMap = new HashMap<>(queueSize);
        for (long i = 0L; i < queueSize; i++) {
            queueMap.put(i, new ConcurrentArrayQueue<>());
        }
        calculateSize = queueSize - 1;
    }

    /**
     * Calculate the next power of 2, greater than or equal to x.
     * <p>
     * From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
     *
     * @param x Value to round up
     * @return The next power of 2 from x inclusive
     */
    public static int ceilingNextPowerOfTwo(final int x) {
        return 1 << (32 - Integer.numberOfLeadingZeros(x - 1));
    }

    public int queueSize() {
        return queueMap.size();
    }

    public long remainTaskCount() {
        long count = 0;
        for (ConcurrentArrayQueue<BaseTask> list : queueMap.values()) {
            count += list.size();
        }
        return count;
    }

    /**
     * 接受消息环
     *
     * @param uniqueId 序列唯一id
     */
    public void publishTask(long uniqueId, BaseTask task) {
        ConcurrentArrayQueue<BaseTask> queue = queueMap.get(Math.abs(uniqueId & calculateSize));
        if (queue.addOne(task) == 1) {
            taskDisruptor.safePublish(task);
        }
    }

    /**
     * 直接停止消息环处理，并记录停止消息环的完成信息。
     */
    public void shutDownNow() {
        try {
            // 开始停止消息环流程，记录开始时间并打印日志。
            LogUtil.common("开始停止队列消息环, name: ", taskDisruptor.getDisruptorName());
            long startTime = System.currentTimeMillis();

            // 强制停止Disruptor，这次没有超时限制
            taskDisruptor.getStatus().set(true);
            taskDisruptor.stop();

            // 记录停止消息环的完成信息，包括耗时
            LogUtil.common("停止队列消息环 name: ", taskDisruptor.getDisruptorName(), ", 耗时: ",
                    System.currentTimeMillis() - startTime, "ms");
        } catch (Throwable t) {
            // 捕获任何在停止过程中抛出的异常，确保停止过程不会因为异常而中断。
            LogUtil.error("停止消息环报错: ", t);
        }
    }

    /**
     * 安全优雅地停止消息环处理。确保所有待处理的任务都被处理完，并在指定时间内尝试停止Disruptor。
     * 这是一个典型的尝试多次停止逻辑，它在处理系统停止时提供了灵活性和安全性。
     */
    public void shutdownGracefully() {
        try {
            // 开始停止消息环流程，记录开始时间并打印日志。
            LogUtil.common("开始停止队列消息环, name: ", taskDisruptor.getDisruptorName());
            long startTime = System.currentTimeMillis();

            // 设置重试次数，确保能够多次尝试停止消息环
            int retry = 10;
            while (retry-- > 0) {
                // 休眠一段时间后检查剩余任务数量
                ThreadUtil.sleep(500);
                while (remainTaskCount() > 0) {
                    // 如果有剩余任务，则休眠更长时间，确保任务处理完毕
                    ThreadUtil.sleep(NumberUtil.THOUSAND);
                }
            }

            // 定义5分钟的时间限制，确保停止过程不会无限期进行
            // 尝试停止Disruptor，并设定超时时间
            taskDisruptor.timeoutStop();
            // 强制停止Disruptor，这次没有超时限制
            taskDisruptor.getStatus().set(true);
            // 记录停止消息环的完成信息，包括耗时
            LogUtil.common("停止队列消息环 name: ", taskDisruptor.getDisruptorName(), ", 耗时: ",
                    System.currentTimeMillis() - startTime, "ms");
        } catch (Throwable t) {
            // 捕获任何在停止过程中抛出的异常，确保停止过程不会因为异常而中断。
            LogUtil.error("停止消息环报错: ", t);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderedQueueDisruptor disruptor = (OrderedQueueDisruptor) o;
        return taskDisruptor.getDisruptorName().equals(disruptor.taskDisruptor.getDisruptorName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskDisruptor.getDisruptorName());
    }
}
