package org.playmore.api.disruptor;

import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Getter;
import org.playmore.api.disruptor.handler.EventErrorHandler;
import org.playmore.api.disruptor.handler.TaskEventBuffer;
import org.playmore.api.disruptor.handler.TaskEventHandler;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.api.disruptor.thread.ITimerEvent;
import org.playmore.api.disruptor.thread.TimerThread;
import org.playmore.common.util.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName SingleDisruptor
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:29
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:29
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class SingleDisruptor {
    /**
     * 是否停止
     */
    @Getter
    private final AtomicBoolean stopping = new AtomicBoolean(false);
    /**
     * 消息环
     */
    private final Disruptor<TaskEventBuffer<BaseTask>> disruptor;
    /**
     * 计时线程
     */
    private TimerThread timer;
    /**
     * 消息环名称
     */
    @Getter
    private String disruptorName;
    /**
     * 心跳间隔
     */
    @Getter
    protected int heart;
    @Getter
    private long consumerThreadId;

    public SingleDisruptor(String disruptorName, int heart, int bufferSize, WaitStrategy waitStrategy) {
        this.disruptorName = disruptorName + "-disruptor";
        this.heart = heart;
        if (this.heart > 0) {
            timer = new TimerThread(this);
        }

        disruptor = new Disruptor<>(
                TaskEventBuffer::new,
                bufferSize,
                r -> {
                    ThreadGroup group = Thread.currentThread().getThreadGroup();
                    Thread t = new Thread(group, r, disruptorName + "-1");
                    t.setDaemon(false);
                    consumerThreadId = t.threadId();
                    return t;
                },
                ProducerType.MULTI,
                waitStrategy);

        TaskEventHandler handler = new TaskEventHandler(stopping);
        disruptor.handleEventsWith(handler);
        disruptor.setDefaultExceptionHandler(EventErrorHandler.getInstance());
    }

    public void start() {
        disruptor.start();
        if (timer != null) {
            timer.start();
        }
        LogUtil.common(disruptorName, "消息环消费者线程包含: ", consumerThreadId);
        LogUtil.start(disruptorName + " disruptor:" + disruptor + " 启动success");
    }

    /**
     * 添加命令
     *
     * @param command 命令
     */
    public void addCommand(BaseTask command) {
        try {
            long next = disruptor.getRingBuffer().next();
            TaskEventBuffer<BaseTask> buffer = disruptor.getRingBuffer().get(next);
            buffer.setTask(command);
            disruptor.getRingBuffer().publish(next);
        } catch (Exception e) {
            LogUtil.error("Server Thread " + disruptor + " Notify Exception", e);
        }
    }

    public void stop(boolean flag) {
        if (stopping.compareAndSet(false, true)) {
            if (timer != null) {
                timer.stop(flag);
            }

            disruptor.shutdown();
        }
    }

    /**
     * 添加定时事件
     *
     * @param event 定时事件
     */
    public void addTimerEvent(ITimerEvent event) {
        if (timer != null) {
            timer.addTimerEvent(event);
        } else {
            LogUtil.error("TimerThread is null, addTimerEvent fail, event: ", event.getClass().getName());
        }
    }
}
