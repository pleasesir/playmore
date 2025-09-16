package org.playmore.gateway.bootstrap;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.lang.ShutdownHookCallback;
import org.apache.dubbo.common.lang.ShutdownHookCallbacks;
import org.apache.dubbo.config.spring.util.DubboBeanUtils;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.playmore.api.config.AppContext;
import org.playmore.common.component.ComponentLifecycle;
import org.playmore.common.util.LogUtil;
import org.playmore.gateway.net.codec.factory.MessageCodecFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @ClassName GateBootstrap
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/9/2 23:26
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/9/2 23:26
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Component
@Slf4j
public class GateServerBootstrap {
    @Resource
    private List<ComponentLifecycle<?>> components;

    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private volatile int gatewayStatus = ST_NOT_STARTED;
    private static final AtomicIntegerFieldUpdater<GateServerBootstrap> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(GateServerBootstrap.class, "gatewayStatus");

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

    /**
     * 添加停服钩子
     */
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
                if (STATE_UPDATER.compareAndSet(GateServerBootstrap.this, ST_SHUTTING_DOWN, ST_SHUTDOWN)) {
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

    /**
     * 服务器启动状态
     *
     * @return 启动状态
     */
    public boolean isStarted() {
        return gatewayStatus == ST_STARTED;
    }
}
