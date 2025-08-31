package org.playmore.gateway.config;

import jakarta.annotation.Nullable;
import org.apache.dubbo.common.deploy.DeployState;
import org.apache.dubbo.config.spring.context.event.DubboModuleStateEvent;
import org.apache.dubbo.rpc.RpcContext;
import org.playmore.api.config.AppContext;
import org.playmore.api.rpc.game.GameServerRpcService;
import org.playmore.common.constant.DubboConst;
import org.playmore.common.msg.impl.GatewayMsg;
import org.playmore.common.util.LogUtil;
import org.playmore.gateway.component.GateServerComponent;
import org.playmore.gateway.net.SocketStatus;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName GatewayOfflineListener
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/31 23:38
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/31 23:38
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Component
public class GatewayOfflineListener implements ApplicationListener<DubboModuleStateEvent> {
    @Override
    public void onApplicationEvent(@Nullable DubboModuleStateEvent event) {
        if (event == null || event.getState() != DeployState.STOPPING) {
            return;
        }

        try {
            GateServerComponent gatewayServer = AppContext.getBean(GateServerComponent.class);
            LogUtil.common("通知游戏服务网关服务下线! gatewayId: ", gatewayServer.getGatewayId());
            GatewayMsg rqMsg = GatewayMsg.serverClose(gatewayServer.getGatewayId(), SocketStatus.GATEWAY_SERVER_CLOSING);
            RpcContext.getClientAttachment().setAttachment(DubboConst.PROVIDER_ID, 0);
            AppContext.getBean(GameServerRpcService.class).cast(rqMsg);
        } catch (Exception ex) {
            LogUtil.error(ex);
        }
    }
}
