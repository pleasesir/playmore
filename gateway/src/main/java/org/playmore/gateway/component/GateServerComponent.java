package org.playmore.gateway.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.playmore.common.component.ComponentLifecycle;
import org.playmore.common.util.Environment;
import org.playmore.common.util.LogUtil;
import org.playmore.gateway.config.GatewayOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName GatewayComponent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 23:58
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 23:58
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Slf4j
@Component
public class GateServerComponent implements ComponentLifecycle<GatewayOrder> {

    @Value("${spring.application.name:}")
    private String serverName;
    @Getter
    @Value("${gateway.port}")
    private int port;
    @Getter
    @Value("${gateway.provider.id}")
    private int gatewayId;
    @Getter
    @Value("${dubbo.registry.address}")
    private String nacosAddress;
    @Getter
    @Value("${environment:}")
    private String environment;
    @Getter
    @Value("${check.connect.second.interval}")
    private int checkConnectInterval;

    /**
     * 代理游戏服与玩家建立连接
     */
    @Getter
    private GateNetComponent gatewayNettyServer;

    @Override
    public String name() {
        return serverName + "-" + gatewayId;
    }

    @Override
    public void start() {
        try {
            //启动网络服务
            startNetServer();
        } catch (Exception e) {
            LogUtil.error("网关启动失败!!!", e);
            System.exit(1);
        }
    }

    @Override
    public void afterStart() {

    }

    @Override
    public void beforeStop() {

    }

    public void startNetServer() throws Exception {
        gatewayNettyServer = new GateNetComponent(port, 32, 32);
        Thread reverseServerThread = new Thread(gatewayNettyServer);
        reverseServerThread.start();
    }

    @Override
    public void stop() {
        //关闭网络连接
        gatewayNettyServer.stop();
    }

    @Override
    public GatewayOrder order() {
        return GatewayOrder.GATEWAY_SERVER;
    }

    public boolean isRelease() {
        return Environment.isRelease(environment);
    }
}
