package org.playmore.game.component.net.task;

import org.playmore.api.config.AppContext;
import org.playmore.api.disruptor.task.impl.AsyncTask;
import org.playmore.api.rpc.consumer.RpcGatewayConsumer;
import org.playmore.common.msg.impl.BatchGatewayMsg;

/**
 * @ClassName BatchGatewayRsTask
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:39
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:39
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class BatchGatewayRsTask extends AsyncTask {

    private final int gatewayId;
    private final BatchGatewayMsg msg;

    public BatchGatewayRsTask(int gatewayId, BatchGatewayMsg msg) {
        super(System.currentTimeMillis());
        this.gatewayId = gatewayId;
        this.msg = msg;
    }

    @Override
    protected void actionBefore(Object... args) {
    }

    @Override
    protected void onCompletion() {
    }

    @Override
    protected void action() throws Exception {
        AppContext.getBean(RpcGatewayConsumer.class).batchAsyncMessage(gatewayId, msg);
    }
}
