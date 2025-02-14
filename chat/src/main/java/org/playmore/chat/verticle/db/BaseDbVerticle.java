//package org.playmore.chat.verticle.db;
//
//import io.vertx.core.Future;
//import org.playmore.common.verticle.BaseVerticle;
//
//import java.util.Objects;
//
/// **
// * db verticle
// *
// * @Author: zhangpeng
// * @Date: 2025/02/13/20:40
// * @Description:
// */
//public class BaseDbVerticle extends BaseVerticle {
//
//    protected Pool mysqlPool;
//
//    protected RowSet<Row> query(String sql) {
//        if (sql == null || sql.isBlank()) {
//            return null;
//        }
//
//        SqlConnection connection = null;
//        try {
//            connection = Future.await(mysqlPool.getConnection());
//            return Future.await(connection.query(sql).execute());
//        } finally {
//            if (Objects.nonNull(connection)) {
//                connection.close();
//            }
//        }
//    }
//
//}
