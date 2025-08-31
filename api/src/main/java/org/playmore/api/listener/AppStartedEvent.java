package org.playmore.api.listener;

import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.time.Clock;

/**
 * @ClassName AppStartedEvent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/31 23:41
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/31 23:41
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class AppStartedEvent extends ApplicationEvent {
    @Serial
    private static final long serialVersionUID = 9036962395005093020L;

    public AppStartedEvent(Object source) {
        super(source);
    }

    public AppStartedEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
