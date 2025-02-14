//package org.playmore.chat.verticle.db.impl;
//
//import io.vertx.core.Future;
//import io.vertx.core.eventbus.Message;
//import io.vertx.mysqlclient.MySQLBuilder;
//import io.vertx.mysqlclient.MySQLConnectOptions;
//import io.vertx.sqlclient.PoolOptions;
//import io.vertx.sqlclient.Row;
//import io.vertx.sqlclient.RowSet;
//import lombok.extern.slf4j.Slf4j;
//import org.playmore.chat.verticle.db.BaseDbVerticle;
//import org.playmore.common.eventbus.EventbusAddress;
//
/// **
// * 动态库mysql verticle
// *
// * @Author: zhangpeng
// * @Date: 2025/02/13/18:28
// * @Description:
// */
//@Slf4j
//public class DynamicMysqlVerticle extends BaseDbVerticle {
//
//    @Override
//    public void start() throws Exception {
//        log.info("start dynamic mysql verticle 虚拟线程: {}", Thread.currentThread().getName());
//        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
//                .setPort(3306)
//                .setHost("172.16.12.100")
//                .setDatabase("treasure_cross_chat_test")
//                .setUser("root")
//                .setPassword("00000000");
//        // Pool options
//        PoolOptions poolOptions = new PoolOptions()
//                .setMaxSize(5);
//        // Create the pooled client
//        mysqlPool = MySQLBuilder.pool()
//                .with(poolOptions)
//                .connectingTo(connectOptions)
//                .using(vertx)
//                .build();
//
//        registerConsumer(EventbusAddress.CHAT_MESSAGE, this::query);
//        log.info("start dynamic mysql verticle complete 虚拟线程: {}", Thread.currentThread().getName());
//    }
//
//    @Override
//    public void stop() throws Exception {
//        Future.await(mysqlPool.close());
//        System.out.println("dynamic mysql pool closed");
//    }
//
//    private void query(Message<Object> response) {
//        RowSet<Row> rows = query("SELECT * FROM chat_private_room");
//        if (rows.size() > 0) {
//            response.reply("Got " + rows.size() + " rows 虚拟线程: " + Thread.currentThread().getName());
//        }
//    }
//}
