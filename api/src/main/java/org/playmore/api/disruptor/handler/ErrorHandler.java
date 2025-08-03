package org.playmore.api.disruptor.handler;

import com.lmax.disruptor.ExceptionHandler;
import org.playmore.common.util.LogUtil;

/**
 * @ClassName ErrorHandler
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:47
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:47
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class ErrorHandler<TaskEventBuffer> implements ExceptionHandler<TaskEventBuffer> {
    @Override
    public void handleEventException(Throwable ex, long sequence, TaskEventBuffer event) {
        LogUtil.error("", ex);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        LogUtil.error("", ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        LogUtil.error("", ex);
    }
}
