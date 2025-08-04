package org.playmore.chat;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.playmore.chat.verticle.MainVerticle;
import org.playmore.common.verticle.DeployVerticleOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * chat server
 *
 * @Author: zhangpeng
 * @Date: 2025/02/11/10:28
 * @Description:
 */
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
@MapperScan("org.playmore.chat.db.mapper")
@EnableDubbo
public class ChatBootstrap {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ChatBootstrap.class, args);
        Vertx chatServer = Vertx.vertx();
        try {
            chatServer.deployVerticle(MainVerticle.class,
                    new DeployVerticleOptions()).toCompletionStage().toCompletableFuture().join();
            ChatBootstrap.log.info("++++++++++++++++++++++++++start chat server success++++++++++++++++++++++++++");
        } catch (Exception ex) {
            System.exit(1);
        }
    }
}
