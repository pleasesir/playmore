package org.playmore.gateway.component;

import org.playmore.api.disruptor.OrderedQueueDisruptor;
import org.playmore.api.disruptor.TaskDisruptor;
import org.playmore.api.disruptor.task.BaseTask;
import org.playmore.common.component.ComponentLifecycle;
import org.playmore.gateway.config.GatewayOrder;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.playmore.gateway.config.GatewayOrder.GATEWAY_EXECUTOR;
import static org.playmore.gateway.util.JvmUtil.CORE_POOL_SIZE;

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
    private OrderedQueueDisruptor orderDisruptor;

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
        noOrderDisruptor = new TaskDisruptor("confirm", 65536,
                TaskDisruptor.newWaitStrategy(), new AtomicBoolean(true), CORE_POOL_SIZE, -1);
        TaskDisruptor disruptor = new TaskDisruptor("send", 65536,
                TaskDisruptor.newWaitStrategy(), new AtomicBoolean(true),
                CORE_POOL_SIZE, Math.max(1, CORE_POOL_SIZE / 2));
        orderDisruptor = new OrderedQueueDisruptor(disruptor);
    }

    @Override
    public void afterStart() {
        noOrderDisruptor.start();
        orderDisruptor.start();
    }

    @Override
    public void beforeStop() {

    }

    @Override
    public void stop() {

    }

    @Override
    public GatewayOrder order() {
        return GATEWAY_EXECUTOR;
    }
}
