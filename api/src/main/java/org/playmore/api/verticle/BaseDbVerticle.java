package org.playmore.api.verticle;


import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.*;
import lombok.extern.slf4j.Slf4j;
import org.playmore.common.util.LogUtil;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collector;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-29 23:57
 * @description TODO
 */
@Slf4j
public abstract class BaseDbVerticle extends BaseVerticle {
    protected Pool mysqlPool;

    @Override
    public void start() throws Exception {
        super.start();
        init(configPrefix());
    }

    protected abstract String configPrefix();

    @Override
    public void stop() throws Exception {
        Future.await(mysqlPool.close());
        log.info("{} mysql pool closed", this.getClass().getSimpleName());
    }

    /**
     * 初始化mysql连接池
     */
    protected void init(String prefix) throws IOException {
        Properties properties = readProperties("jdbc.properties");
        String host = properties.getProperty(prefix + ".host");
        int port = Integer.parseInt(properties.getProperty(prefix + ".port"));
        String database = properties.getProperty(prefix + ".database");
        String user = properties.getProperty(prefix + ".username");
        String password = properties.getProperty(prefix + ".password");

        // mysql数据库连接
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(port)
                .setHost(host)
                .setDatabase(database)
                .setUser(user)
                .setPassword(password)
                .setReconnectAttempts(3)
                .setReconnectInterval(1000);
        connectOptions.setTcpKeepAlive(true);

        // Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(20)
                .setName("db-pool")
                .setIdleTimeout(1800);

        // Create the pooled client
        mysqlPool = MySQLBuilder.pool()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(vertx)
                .build();
    }

    /**
     * 操作数据库
     *
     * @param sql sql
     * @return 执行sql的结果
     */
    protected RowSet<Row> operateDb(String sql) {
        if (sql == null || sql.isBlank()) {
            return null;
        }

        return Future.await(mysqlPool.withConnection(ar -> ar.query(sql).execute()));
    }

    /**
     * 操作数据库
     *
     * @param sql    sql
     * @param params 参数
     * @return 返回执行sql的结果
     */
    protected RowSet<Row> operateByParams(String sql, Tuple params) {
        if (sql == null || sql.isBlank()) {
            return null;
        }

        return Future.await(mysqlPool.withConnection(ar -> {
            try {
                PreparedQuery<RowSet<Row>> prepare = ar.preparedQuery(sql);
                if (params == null) {
                    return prepare.execute();
                } else {
                    return prepare.execute(params);
                }
            } catch (Exception ex) {
                LogUtil.error("sql: ", sql, ", ex: ", ex);
                throw ex;
            }
        }));
    }


    /**
     * 查询收集数据
     *
     * @param sql       sql语句
     * @param params    sql参数
     * @param collector 收集器
     * @param <R>       收集结果集
     * @return 收集结果集
     */
    public <R> R collectingQuery(String sql, Tuple params, Collector<Row, ?, R> collector) {
        if (sql == null || sql.isBlank()) {
            return null;
        }

        return Future.await(mysqlPool.withConnection(cn -> {
            PreparedQuery<RowSet<Row>> prepare = cn.preparedQuery(sql);
            PreparedQuery<SqlResult<R>> collect = prepare.collecting(collector);
            if (params == null) {
                return collect.execute();
            } else {
                return collect.execute(params);
            }
        })).value();
    }

    public RowSet<Row> batchInsert(String sql, List<Tuple> params) {
        if (sql == null || sql.isBlank()) {
            return null;
        }

        return Future.await(mysqlPool.withConnection(cn -> {
            PreparedQuery<RowSet<Row>> prepare = cn.preparedQuery(sql);
            return prepare.executeBatch(params);
        }));
    }
}
