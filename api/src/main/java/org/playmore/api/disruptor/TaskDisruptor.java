package org.playmore.api.disruptor;

import cn.hutool.core.thread.NamedThreadFactory;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.playmore.api.disruptor.executor.AsyncMsgExecutor;
import org.playmore.api.disruptor.handler.ErrorHandler;
import org.playmore.api.disruptor.handler.TaskEventBuffer;
import org.playmore.api.disruptor.handler.TaskEventHandler;
import org.playmore.api.disruptor.handler.TaskWorkHandler;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.common.util.CheckNull;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.NumberUtil;
import org.playmore.common.util.ThreadUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.playmore.api.disruptor.DisruptorConstant.*;

/**
 * @ClassName TaskDisruptor
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:37
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:37
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class TaskDisruptor {
    private static volatile AsyncMsgExecutor asyncMsgExecutor;
    @Getter
    private final String disruptorName;
    private final Disruptor<TaskEventBuffer<BaseTask>> disruptor;
    @Getter
    private final AtomicBoolean status;
    @Getter
    private final WaitStrategy waitStrategy;
    @Getter
    private TaskWorkHandler[] handlers;
    private TaskEventHandler handler;
    private Set<Long> consumerThreadIdSet;
    private boolean asyncPublish;

    public TaskDisruptor(String threadName, int bufferSize, WaitStrategy waitStrategy, AtomicBoolean status) {
        disruptor = new Disruptor<>(
                TaskEventBuffer::new,
                bufferSize,
                new NamedThreadFactory(threadName, false),
                ProducerType.MULTI,
                waitStrategy
        );

        handler = new TaskEventHandler(status);
        disruptor.handleEventsWith(handler);
        disruptor.setDefaultExceptionHandler(new ErrorHandler<>());
        disruptorName = threadName;
        this.waitStrategy = waitStrategy;
        this.status = status;
    }

    public TaskDisruptor(String threadName, int bufferSize, WaitStrategy waitStrategy,
                         AtomicBoolean status, int multiConsumer, int asyncSize) {
        disruptor = new Disruptor<>(
                TaskEventBuffer::new,
                bufferSize,
                new NamedThreadFactory(threadName, false),
                ProducerType.MULTI,
                waitStrategy
        );

        int consumerSize = multiConsumer;
        TaskWorkHandler[] handlers = new TaskWorkHandler[multiConsumer];
        while (consumerSize-- > 0) {
            handlers[consumerSize] = new TaskWorkHandler(status, this);
        }
        this.handlers = handlers;
        disruptor.handleEventsWithWorkerPool(handlers);
        disruptor.setDefaultExceptionHandler(new ErrorHandler<>());
        disruptorName = threadName;
        this.status = status;
        asyncPublish = asyncSize > 0;
        this.waitStrategy = waitStrategy;
        if (asyncSize > 0) {
            consumerThreadIdSet = new HashSet<>(multiConsumer);
            if (TaskDisruptor.asyncMsgExecutor == null) {
                synchronized (TaskDisruptor.class) {
                    if (TaskDisruptor.asyncMsgExecutor == null) {
                        TaskDisruptor.asyncMsgExecutor = new AsyncMsgExecutor(new ThreadPoolExecutor(asyncSize, asyncSize,
                                30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                                new NamedThreadFactory("async-publish-message-executor", false)),
                                new AtomicInteger());
                    }
                }
            }

            TaskDisruptor.asyncMsgExecutor.counter().getAndIncrement();
        }
    }

    public TaskDisruptor(String threadName, int bufferSize, AtomicBoolean status) {
        disruptor = new Disruptor<>(
                TaskEventBuffer::new,
                bufferSize,
                new NamedThreadFactory(threadName, false),
                ProducerType.MULTI,
                (waitStrategy = new TimeoutBlockingWaitStrategy(5, TimeUnit.SECONDS))
        );

        handler = new TaskEventHandler(status);
        disruptor.handleEventsWith(handler);
        disruptor.setDefaultExceptionHandler(new ErrorHandler<>());
        disruptorName = threadName;
        this.status = status;
    }

    public TaskDisruptor(String threadName, int bufferSize, WaitStrategy waitStrategy, AtomicBoolean status,
                         boolean inspectionTimeConsuming) {
        disruptor = new Disruptor<>(
                TaskEventBuffer::new,
                bufferSize,
                new NamedThreadFactory(threadName, false),
                ProducerType.MULTI,
                waitStrategy
        );

        this.waitStrategy = waitStrategy;
        handler = new TaskEventHandler(status, inspectionTimeConsuming);
        disruptor.handleEventsWith(handler);
        disruptor.setDefaultExceptionHandler(new ErrorHandler<>());
        disruptorName = threadName;
        this.status = status;
    }

    /**
     * 启动Disruptor。
     * 此方法初始化Disruptor并确保所有消费者线程已正确识别和记录。
     * 它使用Java Management Extensions (JMX)来获取当前运行的所有线程的信息，
     * 并将属于Disruptor的消费者线程的ID添加到一个集合中。
     * 这样做的目的是为了更好地监控和管理Disruptor及其相关线程。
     */
    public void start() {
        // 启动Disruptor引擎
        disruptor.start();

        // 如果消费者线程ID集合不为空，则尝试通过JMX获取所有当前线程的信息
        if (Objects.nonNull(consumerThreadIdSet)) {
            // 获取Java虚拟机的线程管理器
            ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
            // 获取所有当前线程的ID
            long[] threadIdArr = threadMxBean.getAllThreadIds();

            // 如果存在线程ID，则遍历这些线程信息
            if (CheckNull.nonEmpty(threadIdArr)) {
                for (long tid : threadIdArr) {
                    // 获取特定线程的信息
                    ThreadInfo ti = threadMxBean.getThreadInfo(tid);
                    // 如果线程信息不存在，或该线程不是Disruptor相关的线程，则跳过
                    if (ti == null || !ti.getThreadName().contains(disruptorName)) {
                        continue;
                    }
                    // 将Disruptor相关的消费者线程ID添加到集合中
                    consumerThreadIdSet.add(ti.getThreadId());
                }
                LogUtil.common(disruptorName, "消息环消费者线程包含: ", ArrayUtils.toString(consumerThreadIdSet));
            }
        }
        // 记录Disruptor启动成功的日志
        LogUtil.common(disruptorName, " 启动成功. 信息: ", disruptor.toString());
    }


    /**
     * 关闭异步消息执行器。
     * 当异步发布启用且异步消息执行器存在且当前任务计数器减少到零或以下时，执行器将被优雅地关闭。
     * 这确保了在没有更多任务需要处理时，执行器资源可以被释放。
     */
    private void shutdownExecutor() {
        // 检查是否应该关闭异步消息执行器
        if (asyncPublish && TaskDisruptor.asyncMsgExecutor.counter().decrementAndGet() <= 0
                && !TaskDisruptor.asyncMsgExecutor.executor().isShutdown()) {
            // 开始关闭异步消息执行器的日志记录
            LogUtil.common("开始停止 async-publish-message-executor...");
            try {
                // 执行优雅地关闭操作，并设置超时时间
                ThreadUtil.shutdownThreadPool(TaskDisruptor.asyncMsgExecutor.executor(), 5, TimeUnit.MINUTES);
                // 关闭完成的日志记录
                LogUtil.common("async-publish-message-executor停止完成");
            } catch (Throwable t) {
                // 捕获并记录关闭过程中可能出现的任何异常
                LogUtil.error("", t);
            }
        }
    }

    public static TimeoutBlockingWaitStrategy newWaitStrategy() {
        return new TimeoutBlockingWaitStrategy(5, TimeUnit.SECONDS);
    }

    public static LiteTimeoutBlockingWaitStrategy newWaitStrategy(int timeout, TimeUnit timeUnit) {
        return new LiteTimeoutBlockingWaitStrategy(timeout, timeUnit);
    }


    public void stop() {
        long startTime = System.nanoTime();
        shutdownExecutor();
        disruptor.halt();
        long endTime = System.nanoTime();
        LogUtil.common(disruptorName, " stopping cost time: ", (endTime - startTime) / NumberUtil.MILLION);
    }

    /**
     * Returns {@code true} if the specified disruptor still has unprocessed events.
     */
    private boolean hasBacklog() {
        final RingBuffer<?> ringBuffer = disruptor.getRingBuffer();
        return !ringBuffer.hasAvailableCapacity(ringBuffer.getBufferSize());
    }

    public void timeoutStop() {
        long startTime = System.nanoTime();

        // Calling Disruptor.shutdown() will wait until all enqueued events are fully processed,
        // but this waiting happens in a busy-spin. To avoid (postpone) wasting CPU,
        // we sleep in short chunks, up to 10 seconds, waiting for the ringBuffer to drain.
        for (int i = 0; hasBacklog() && i < MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN; i++) {
            // give up the CPU for a while
            ThreadUtil.sleep(SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS);
        }

        try {
            disruptor.shutdown(DEFAULT_TIME_OUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // 如果停止操作超时，记录日志并继续尝试
            LogUtil.common("停止队列消息环 name: ", disruptor, ", 等待中..., 等待时间: ",
                    System.currentTimeMillis() - startTime, "ms");
            // give up on remaining log events, if any
            disruptor.halt();
            return;
        }
        long endTime = System.nanoTime();
        LogUtil.common(disruptorName, "timeout stopping cost time: ", (endTime - startTime) / NumberUtil.MILLION);
    }

    /**
     * 发布执行的任务
     *
     * @param task 要执行的任务
     */
    public void publish(BaseTask task) {
        long sequence = getRingBuffer().next();
        TaskEventBuffer<BaseTask> buffer = getRingBuffer().get(sequence);
        buffer.setTask(task);
        disruptor.getRingBuffer().publish(sequence);
        LogUtil.messageDebug("提交task到消息环中, task.traceId: ", task.getTraceId(), ", task.name: ", task.getReq(),
                ", disruptor: ", disruptorName);
    }

    public RingBuffer<TaskEventBuffer<BaseTask>> getRingBuffer() {
        return disruptor.getRingBuffer();
    }

    /**
     * 安全抛入任务到消息环
     *
     * @param task 任务
     */
    public void safePublish(BaseTask task) {
        if (Objects.nonNull(consumerThreadIdSet) &&
                consumerThreadIdSet.contains(Thread.currentThread().threadId())) {
            tryPublish(task);
        } else {
            publish(task);
        }
    }

    /**
     * 尝试发布下一个任务到Disruptor的环形缓冲区中。
     * <p>
     * 此方法尝试从环形缓冲区获取下一个可用的位置来发布任务。如果成功，它将任务设置到指定的位置，并发布这个位置。
     * 如果环形缓冲区没有可用空间，则捕获InsufficientCapacityException异常，并返回false。
     *
     * @param task 要发布的任务对象，必须不为空。
     * @return 如果成功发布任务，则返回true；如果环形缓冲区没有足够的空间来发布任务，则返回false。
     */
    private boolean tryPublishNext(BaseTask task) {
        try {
            // 尝试获取下一个可用的序列号，用于在环形缓冲区中发布事件。
            long sequence = getRingBuffer().tryNext();
            // 根据序列号获取环形缓冲区中的具体位置，用于设置任务。
            TaskEventBuffer<BaseTask> buffer = getRingBuffer().get(sequence);
            // 在缓冲区的当前位置设置任务对象。
            buffer.setTask(task);
            // 发布刚刚设置任务的序列号，使其对消费者可见。
            disruptor.getRingBuffer().publish(sequence);
            // 记录日志，表示任务发布成功。
            LogUtil.messageDebug("尝试提交task到消息环中成功, task.traceId: ", task.getTraceId(), ", task.name: ", task.getReq(),
                    ", disruptor: ", disruptorName);
            return true;
        } catch (InsufficientCapacityException e) {
            // 如果环形缓冲区没有足够的空间，捕获异常并返回false。
            return false;
        }
    }


    /**
     * 尝试发布一个任务到消息环中。如果当前无法直接发布，则尝试使用异步执行器进行发布。
     *
     * @param task 需要发布的任务，实现了ITask接口。
     */
    public void tryPublish(BaseTask task) {
        // 尝试直接将任务发布到消息环中
        if (!tryPublishNext(task)) {
            // 如果直接发布失败，检查异步消息执行器是否为空
            if (Objects.isNull(TaskDisruptor.asyncMsgExecutor)) {
                // 如果异步消息执行器为空，记录错误日志并返回
                LogUtil.error("抛入消息环失败, asyncMsgExecutor is null");
                return;
            }
            // 记录日志，说明任务将通过异步执行器进行发布
            // 如果投递失败, 转入异步线程池投递(高并发场景下可能会投递失败)
            LogUtil.message("尝试抛入消息环失败, 抛入异步线程池. min: ", getRingBuffer().getMinimumGatingSequence(),
                    ", cur: ", getRingBuffer().getCursor());
            // 使用异步执行器执行任务
            TaskDisruptor.asyncMsgExecutor.publish(this, task);
            // 唤醒等待的消费者线程
            waitStrategy.signalAllWhenBlocking();
        }
    }
}
