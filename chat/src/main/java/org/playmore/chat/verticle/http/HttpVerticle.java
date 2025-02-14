package org.playmore.chat.verticle.http;

import io.vertx.core.http.HttpServerOptions;
import lombok.extern.slf4j.Slf4j;
import org.playmore.common.eventbus.EventbusAddress;
import org.playmore.common.verticle.BaseVerticle;

/**
 * @Author: zhangpeng
 * @Date: 2025/02/12/14:35
 * @Description:
 */
@Slf4j
public class HttpVerticle extends BaseVerticle {

    @Override
    public void start() throws Exception {
        log.info("start Http verticle 虚拟线程: {}", Thread.currentThread().getName());
        var httpServer = vertx.createHttpServer(new HttpServerOptions().setLogActivity(true));
        httpServer.requestHandler((request) -> {
            if (request.uri().contains("/chat")) {
                request.response().end((String) eventBusRequest(EventbusAddress.CHAT_MESSAGE, ""));
            } else {
                request.response().end((String) eventBusRequest(EventbusAddress.INI, ""));
            }
        });

        httpServer.listen(8080, "localhost");
        log.info("start Http verticle complete 虚拟线程: {}", Thread.currentThread().getName());
    }
}
