package org.playmore.api.disruptor.handler;

import com.lmax.disruptor.ExceptionHandler;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.common.util.LogUtil;

/**
 * @ClassName EventErrorHandler
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:33
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:33
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class EventErrorHandler implements ExceptionHandler<TaskEventBuffer<BaseTask>> {
    private static class InstanceHolder {
        private static final EventErrorHandler INSTANCE = new EventErrorHandler();
    }

    public static EventErrorHandler getInstance() {
        return InstanceHolder.INSTANCE;
    }


    /**
     * 处理事件异常
     *
     * @param e
     * @param event
     */
    public static void handleException(Throwable e, TaskEventBuffer<BaseTask> event) {
        try {
            BaseTask command = event.task();
//            EventErrorHandler.handleException(e, command);
        } catch (Exception ex) {
            LogUtil.error(ex);
        }
    }

    @Override
    public void handleEventException(Throwable ex, long sequence, TaskEventBuffer<BaseTask> event) {
        EventErrorHandler.handleException(ex, event);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        LogUtil.error("handleOnStartException ex: " + ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        LogUtil.error("handleOnShutdownException ex: " + ex);
    }
}
