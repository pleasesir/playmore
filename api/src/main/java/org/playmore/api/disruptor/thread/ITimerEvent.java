package org.playmore.api.disruptor.thread;

import lombok.Data;
import org.playmore.api.disruptor.task.BaseTask;

/**
 * @ClassName ITimerEvent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:31
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:31
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Data
public abstract class ITimerEvent extends BaseTask {

    private int loop;

    /**
     * @Description: 剩余时间
     */
    public abstract long remain();
}
