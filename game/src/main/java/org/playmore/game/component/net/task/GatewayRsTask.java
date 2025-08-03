package org.playmore.game.component.net.task;

import org.playmore.api.config.AppContext;
import org.playmore.api.disruptor.task.impl.AsyncTask;
import org.playmore.api.rpc.consumer.RpcGatewayConsumer;
import org.playmore.common.msg.impl.GatewayMsg;
import org.playmore.common.util.LogUtil;
import org.playmore.pb.BasePb;

import java.util.Objects;

/**
 * @ClassName GatewayRsTask
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:26
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:26
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class GatewayRsTask extends AsyncTask {

    private final int gatewayId;
    private GatewayMsg msg;
    private BasePb.Base basePb;

    public GatewayRsTask(int gatewayId, GatewayMsg msg, BasePb.Base basePb) {
        super(System.currentTimeMillis());
        this.gatewayId = gatewayId;
        this.msg = msg;
        this.basePb = basePb;
    }

    @Override
    protected void actionBefore(Object... args) {
    }

    @Override
    protected void onCompletion() {
    }

    @Override
    protected void action() throws Exception {
        if (Objects.nonNull(basePb)) {
            LogUtil.traceMessage(basePb, msg.getRoleId());
        }
        AppContext.getBean(RpcGatewayConsumer.class).asyncMessage(gatewayId, msg);
    }

    @Override
    public void clear() {
        super.clear();
        msg = null;
        basePb = null;
    }
}
