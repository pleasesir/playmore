package org.playmore.gateway.component;

import org.playmore.api.disruptor.TaskDisruptor;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.common.component.ComponentLifecycle;
import org.playmore.gateway.config.GatewayOrder;
import org.springframework.stereotype.Component;

/**
 * @ClassName GatewayExecutorComponent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 23:38
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 23:38
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Component
public class GateExecutorComponent implements ComponentLifecycle<GatewayOrder> {

    private TaskDisruptor noOrderDisruptor;

    public void publishNoOrderReceiveTask(BaseTask task) {
        noOrderDisruptor.publish(task);
    }

    public void publishSendTask(int hashId, BaseTask task) {
        noOrderDisruptor.publish(task);
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public void start() {

    }

    @Override
    public void afterStart() {

    }

    @Override
    public void beforeStop() {

    }

    @Override
    public void stop() {

    }

    @Override
    public GatewayOrder order() {
        return null;
    }
}
