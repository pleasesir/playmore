package org.playmore.gateway.net.work;

import org.apache.dubbo.rpc.RpcContext;
import org.playmore.api.config.AppContext;
import org.playmore.api.disruptor.task.impl.AsyncTask;
import org.playmore.api.rpc.game.GameServerRpcService;
import org.playmore.common.constant.DubboConst;
import org.playmore.common.msg.impl.GatewayMsg;

/**
 * @ClassName ForwardGameWork
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/29 00:29
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/29 00:29
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class ForwardGameWork extends AsyncTask {

    private final int gameServerId;
    private final GatewayMsg msg;

    public ForwardGameWork(int gameServerId, GatewayMsg msg) {
        super(System.currentTimeMillis());
        this.gameServerId = gameServerId;
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
        RpcContext.getClientAttachment().setObjectAttachment(DubboConst.PROVIDER_ID, gameServerId);
        AppContext.getBean(GameServerRpcService.class).cast(msg);
    }
}
