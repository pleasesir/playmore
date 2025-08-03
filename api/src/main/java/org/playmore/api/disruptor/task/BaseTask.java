package org.playmore.api.disruptor.task;

import lombok.Getter;
import lombok.Setter;
import org.playmore.common.util.ExceptionUtil;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.MDCUtils;
import org.slf4j.MDC;

import java.util.Objects;
import java.util.Queue;

/**
 * @ClassName BaseTask
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:41
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:41
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public abstract class BaseTask implements Runnable {
    @Setter
    protected Queue<BaseTask> queue;
    @Getter
    protected String traceId;
    protected Throwable t;
    protected long startTime;
    protected String req;

    public BaseTask() {
        traceId = MDCUtils.getTraceId();
        req = LogUtil.getReq(this.getClass());
        startTime = System.currentTimeMillis();
    }

    public BaseTask(long startTime) {
        this.startTime = startTime;
    }

    public String getReq() {
        if (req != null) {
            return req;
        }

        return getClass().getSimpleName();
    }

    public void clear() {
        req = null;
        t = null;
        traceId = null;
        queue = null;
    }

    public Queue<BaseTask> queue() {
        return queue;
    }

    @Override
    public void run() {
        try {
            try {
                MDC.put(MDCUtils.TRACE_ID, traceId);
                actionBefore();
            } catch (Throwable t) {
                ExceptionUtil.analysisThrowable(getClass(), t);
                this.t = t;
            }

            if (Objects.isNull(t)) {
                try {
                    action();
                } catch (Throwable t) {
                    this.t = t;
                }
            }

            if (Objects.nonNull(t)) {
                try {
                    handleInvokeThrowable(t);
                } catch (Throwable t) {
                    ExceptionUtil.analysisThrowable(getClass(), t);
                }
            }

            try {
                onCompletion();
            } catch (Throwable t) {
                ExceptionUtil.analysisThrowable(getClass(), t);
            }
        } finally {
            MDC.clear();
        }
    }

    public long startTime() {
        return startTime;
    }

    /**
     * 执行当前task任务逻辑前
     *
     * @param args
     */
    protected abstract void actionBefore(Object... args);

    /**
     * 执行当前task任务逻辑
     */
    protected abstract void action() throws Exception;

    /**
     * 处理报错
     *
     * @param t
     */
    protected abstract void handleInvokeThrowable(Throwable t);

    /**
     * 完成任务
     */
    protected abstract void onCompletion();
}
