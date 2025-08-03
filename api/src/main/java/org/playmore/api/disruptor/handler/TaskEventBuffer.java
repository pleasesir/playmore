package org.playmore.api.disruptor.handler;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.playmore.api.disruptor.task.BaseTask;

import java.util.Objects;

/**
 * @ClassName TaskEventBuffer
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:46
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:46
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Setter
public class TaskEventBuffer<T extends BaseTask> {
    private T task;

    public void execute() throws Throwable {
        task.run();
    }

    public String req() {
        if (Objects.nonNull(task)) {
            return task.getReq();
        }

        return StringUtils.EMPTY;
    }

    public void clear() {
        task.clear();
        task = null;
    }

    public BaseTask task() {
        return task;
    }

    public long startTime() {
        return task.startTime();
    }

    public String traceId() {
        return task.getTraceId();
    }
}
