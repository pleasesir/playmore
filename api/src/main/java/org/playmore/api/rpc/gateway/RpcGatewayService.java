package org.playmore.api.rpc.gateway;

import org.playmore.common.msg.impl.BatchGatewayMsg;
import org.playmore.common.msg.impl.GatewayMsg;

/**
 * @ClassName RpcGatewayService
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:36
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:36
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface RpcGatewayService {
    /**
     * 异步处理游戏服回包
     *
     * @param msg 回包
     */
    void asyncGameMessage(GatewayMsg msg);

    /**
     * 批量异步网关回包
     *
     * @param msg 回包
     */
    void batchAsyncGameMessage(BatchGatewayMsg msg);
}
