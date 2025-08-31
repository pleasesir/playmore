package org.playmore.gateway.component;

import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.lang.ShutdownHookCallback;
import org.apache.dubbo.common.lang.ShutdownHookCallbacks;
import org.apache.dubbo.config.spring.util.DubboBeanUtils;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.playmore.api.config.AppContext;
import org.playmore.common.component.ComponentLifecycle;
import org.playmore.common.util.Environment;
import org.playmore.common.util.LogUtil;
import org.playmore.gateway.config.GatewayOrder;
import org.playmore.gateway.net.codec.factory.MessageCodecFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

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
    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private static final AtomicIntegerFieldUpdater<GateServerComponent> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(GateServerComponent.class, "gatewayStatus");

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
    @Resource
    private List<ComponentLifecycle<?>> components;
    private volatile int gatewayStatus = ST_NOT_STARTED;

    /**
     * 代理游戏服与玩家建立连接
     */
    @Getter
    private GateNetComponent gatewayNettyServer;

    @Override
    public String name() {
        return serverName + "-" + gatewayId;
    }

    /**
     * 初始化网关服务器
     */
    public void init() {
        components.stream()
                .sorted(Comparator.comparing(ComponentLifecycle::order))
                .forEach(gatewayComponent -> {
                    gatewayComponent.start();
                    gatewayComponent.afterStart();
                });

        MessageCodecFactory.initData();
        addShutDownHook();
        // 修改服务器状态
        STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED);
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

    /**
     * 服务器启动状态
     *
     * @return 启动状态
     */
    public boolean isStarted() {
        return gatewayStatus == ST_STARTED;
    }

    /**
     * 添加停服钩子
     */
    @SuppressWarnings("unchecked")
    public void addShutDownHook() {
        ApplicationModel applicationModel = DubboBeanUtils.getApplicationModel(AppContext.getContext());
        if (applicationModel == null) {
            log.error("applicationModel is null");
            return;
        }

        ShutdownHookCallbacks shutdownHookCallbacks =
                applicationModel.getBeanFactory().getBean(ShutdownHookCallbacks.class);
        shutdownHookCallbacks.addCallback(new ShutdownHookCallback() {
            @Override
            public void callback() {
                if (STATE_UPDATER.compareAndSet(GateServerComponent.this, ST_SHUTTING_DOWN, ST_SHUTDOWN)) {
                    components.stream()
                            .sorted(Comparator.comparing(ComponentLifecycle::order))
                            .forEach(life -> {
                                life.beforeStop();
                                life.stop();
                            });
                    LogUtil.common("*******************************网关服务器停服成功*******************************");
                }
            }

            @Override
            public int getPriority() {
                return Ordered.HIGHEST_PRECEDENCE + 1;
            }
        });
    }

}
