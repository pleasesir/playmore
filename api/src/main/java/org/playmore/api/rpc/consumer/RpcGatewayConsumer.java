package org.playmore.api.rpc.consumer;

import org.apache.dubbo.rpc.RpcContext;
import org.playmore.api.config.AppContext;
import org.playmore.api.rpc.gateway.RpcGatewayService;
import org.playmore.common.constant.DubboConst;
import org.playmore.common.msg.impl.BatchGatewayMsg;
import org.playmore.common.msg.impl.GatewayMsg;

/**
 * @ClassName RpcGatewayConsumer
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:28
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:28
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class RpcGatewayConsumer {

    /**
     * 异步返回网关消息
     *
     * @param providerId 网关id
     * @param msg        网关消息
     */
    public void asyncMessage(int providerId, GatewayMsg msg) {
        RpcContext.getClientAttachment().setAttachment(DubboConst.PROVIDER_ID, providerId);
        AppContext.getBean(RpcGatewayService.class).asyncGameMessage(msg);
    }

    /**
     * 异步批量返回网关消息
     *
     * @param providerId 网关id
     * @param msg        网关消息
     */
    public void batchAsyncMessage(int providerId, BatchGatewayMsg msg) {
        RpcContext.getClientAttachment().setAttachment(DubboConst.PROVIDER_ID, providerId);
        AppContext.getBean(RpcGatewayService.class).batchAsyncGameMessage(msg);
    }
}