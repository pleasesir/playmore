package org.playmore.api.disruptor.task.impl;

import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.api.util.VertxUtil;
import org.playmore.api.verticle.eventbus.event.impl.TaskEvent;
import org.playmore.common.util.ExceptionUtil;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.MDCUtils;
import org.slf4j.MDC;

/**
 * @ClassName AsyncTask
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:43
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:43
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public abstract class AsyncTask extends BaseTask {
    public AsyncTask() {
        super();
    }

    public AsyncTask(long startTime) {
        super(startTime);
    }

    @Override
    protected void actionBefore(Object... args) {
        LogUtil.handleTaskLogBegin(getReq());
    }

    @Override
    protected void handleInvokeThrowable(Throwable t) {
        ExceptionUtil.analysisThrowable(getClass(), t);
    }

    @Override
    protected void onCompletion() {
        LogUtil.handleTaskLogEnd(getReq(), MDC.get(MDCUtils.TRACE_ID));
    }

    public void sendTask(TaskEvent address, String... params) {
        String eventAddress = VertxUtil.buildAddress(address, params);
        VertxUtil.sendEvent(eventAddress, this);
    }
}
