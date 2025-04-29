package org.playmore.api.verticle.msg;


import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.Data;

import java.util.List;
import java.util.stream.Collector;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-29 23:56
 * @description TODO
 */
@Data
public class ExecuteSqlMsg {
    private final String sql;
    private Tuple params;
    private Collector<Row, ?, ?> collector;
    private List<Tuple> batchParams;

    public ExecuteSqlMsg(String sql, Tuple params) {
        this.sql = sql;
        this.params = params;
    }

    public ExecuteSqlMsg(String sql, Object... params) {
        this.sql = sql;
        if (params != null && params.length > 0) {
            this.params = Tuple.from(params);
        }
    }

    public ExecuteSqlMsg(String sql, Collector<Row, ?, ?> collector, Object... params) {
        this.sql = sql;
        this.collector = collector;
        if (params != null && params.length > 0) {
            this.params = Tuple.from(params);
        }
    }

    public ExecuteSqlMsg(String sql, List<Tuple> batchParams) {
        this.sql = sql;
        this.batchParams = batchParams;
    }

    @Override
    public String toString() {
        return "ExecuteSqlMsg{" +
                "sql='" + sql + '\'' +
                ", params=" + params +
                ", collector=" + collector +
                ", batchParams=" + batchParams +
                '}';
    }
}
