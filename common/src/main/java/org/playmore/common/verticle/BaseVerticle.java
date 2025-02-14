package org.playmore.common.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import org.playmore.common.eventbus.EventbusAddress;

/**
 * @Author: zhangpeng
 * @Date: 2025/02/13/16:57
 * @Description:
 */
public class BaseVerticle extends AbstractVerticle {

    /**
     * vertx事件总线发送消息, 并等待事件结果
     *
     * @param address
     * @param message
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T> T eventBusRequest(EventbusAddress address, Object message) {
        return (T) Future.await(vertx.eventBus().request(address.name(), message)).body();
    }

    /**
     * 注册消费者
     *
     * @param address
     * @param handler
     * @param <T>
     */
    protected <T> void registerConsumer(EventbusAddress address, Handler<Message<T>> handler) {
        vertx.eventBus().consumer(address.name(), handler);
    }
}
