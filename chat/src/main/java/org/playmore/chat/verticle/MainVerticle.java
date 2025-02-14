package org.playmore.chat.verticle;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;
import org.playmore.chat.verticle.http.HttpVerticle;
import org.playmore.common.verticle.BaseVerticle;
import org.playmore.common.verticle.DeployVerticleOptions;

import java.util.List;

/**
 * @Author: zhangpeng
 * @Date: 2025/02/13/19:10
 * @Description:
 */
@Slf4j
public class MainVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> promise) throws Exception {
        Future<String> httpServer = vertx.deployVerticle(HttpVerticle.class,
                new DeployVerticleOptions().setInstances(1));
//        Future<String> dynamicMysql = vertx.deployVerticle(DynamicMysqlVerticle.class,
//                new DeployVerticleOptions().setInstances(1));
//        Future<String> staticMysql = vertx.deployVerticle(StaticMysqlVerticle.class,
//                new DeployVerticleOptions().setInstances(1));

        // 打印启动结果
        Future.all(List.of(httpServer)).onComplete(ar -> {
            if (ar.succeeded()) {
                promise.complete();
                log.info("start main verticle complete");
            } else {
                promise.fail(ar.cause());
            }
        }).onFailure(Throwable::printStackTrace);
    }
}
