package org.playmore.api.verticle.db.impl;


import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.playmore.api.annotation.Subscribe;
import org.playmore.api.verticle.BaseDbVerticle;
import org.playmore.api.verticle.eventbus.event.impl.DatabaseEvent;
import org.playmore.api.verticle.msg.ExecuteSqlMsg;
import org.playmore.common.util.LogUtil;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-30 0:08
 * @description TODO
 */
public class DynamicDbVerticle extends BaseDbVerticle {
    @Override
    public void start() throws Exception {
        try {
            LogUtil.start("start dynamic db verticle...");
            super.start();
            LogUtil.start("start dynamic db verticle complete");
        } catch (Exception e) {
            LogUtil.start("start dynamic db verticle error" + e);
            System.exit(1);
        }
    }

    @Override
    protected String configPrefix() {
        return "jdbc.dynamic";
    }

    @Subscribe(dbEvent = DatabaseEvent.DYNAMIC_OPERATE_DB)
    public RowSet<Row> operateDb(ExecuteSqlMsg msg) {
        return operateByParams(msg.getSql(), msg.getParams());
    }

    @SuppressWarnings("unchecked")
    @Subscribe(dbEvent = DatabaseEvent.DYNAMIC_COLLECT_DB)
    public <R> R collectingQuery(ExecuteSqlMsg msg) {
        return (R) collectingQuery(msg.getSql(), msg.getParams(), msg.getCollector());
    }

    @Subscribe(dbEvent = DatabaseEvent.DYNAMIC_BATCH_INSERT)
    public RowSet<Row> batchInsert(ExecuteSqlMsg msg) {
        return batchInsert(msg.getSql(), msg.getBatchParams());
    }
}
