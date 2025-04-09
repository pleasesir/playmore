package org.playmore.chat.verticle.db.impl;

import io.vertx.core.eventbus.Message;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.extern.slf4j.Slf4j;
import org.playmore.chat.verticle.db.BaseDbVerticle;
import org.playmore.common.eventbus.EventbusAddress;

/**
 * 静态mysql库
 *
 * @Author: zhangpeng
 * @Date: 2025/02/13/18:35
 * @Description:
 */
@Slf4j
public class StaticMysqlVerticle extends BaseDbVerticle {

    @Override
    public void start() throws Exception {
        log.info("start static mysql verticle 虚拟线程: {}", Thread.currentThread().getName());
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("172.16.12.100")
                .setDatabase("treasure_ini_test")
                .setUser("root")
                .setPassword("00000000");
        // Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);
        // Create the pooled client
        mysqlPool = MySQLBuilder.pool()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(vertx)
                .build();

        registerConsumer(EventbusAddress.INI, this::query);
        log.info("start static mysql verticle complete 虚拟线程: {}", Thread.currentThread().getName());
    }

    @Override
    public void stop() throws Exception {
        mysqlPool.close(event -> {
            if (event.succeeded()) {
                System.out.println("mysql pool closed");
            } else {
                System.out.println("mysql pool close failed");
            }
        });
    }

    private void query(Message<Object> response) {
        RowSet<Row> rows = query("SELECT * FROM s_system");
        if (rows.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Row row : rows) {
                sb.append(row.getString("desc")).append(",");
            }
            response.reply("Got " + sb.toString() + " rows 虚拟线程: " + Thread.currentThread().getName());
        }
    }
}
