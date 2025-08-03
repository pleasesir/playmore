package org.playmore.api.disruptor.handler;

import com.lmax.disruptor.EventHandler;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.common.util.ExceptionUtil;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.NumberUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName TaskEventHandler
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:46
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:46
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class TaskEventHandler implements EventHandler<TaskEventBuffer<BaseTask>> {
    private final AtomicBoolean stopping;
    private final boolean inspectionTimeConsuming;

    public TaskEventHandler(AtomicBoolean stopping) {
        this(stopping, true);
    }

    public TaskEventHandler(AtomicBoolean stopping, boolean inspectionTimeConsuming) {
        this.stopping = stopping;
        this.inspectionTimeConsuming = inspectionTimeConsuming;
    }

    public boolean isSameThread() {
        return false;
    }

    @Override
    public void onEvent(TaskEventBuffer<BaseTask> event, long sequence, boolean endOfBatch) {
        try {
            if (stopping.get()) {
                return;
            }
            event.execute();
            if (inspectionTimeConsuming) {
                long duration;
                if ((duration = System.currentTimeMillis() - event.startTime()) >= NumberUtil.HALF_OF_HUNDRED) {
                    LogUtil.warn("taskUnit take more than 50ms, spend times: ", duration, "ms, execute service: ", event.req(),
                            ", traceId: ", event.traceId());
                }
            }
        } catch (Throwable ex) {
            ExceptionUtil.analysisThrowable(getClass(), ex);
        } finally {
            event.clear();
        }
    }
}
