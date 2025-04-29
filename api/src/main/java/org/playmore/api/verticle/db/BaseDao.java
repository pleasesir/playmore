package org.playmore.api.verticle.db;


import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.sqlclient.Row;
import org.playmore.api.util.VertxUtil;
import org.playmore.api.verticle.eventbus.event.impl.DatabaseEvent;
import org.playmore.api.verticle.msg.ExecuteSqlMsg;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.playmore.common.util.TimeHelper.SYSTEM_ZONE;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-30 0:04
 * @description TODO
 */
public class BaseDao {
    /**
     * vertx事件总线发送消息, 并等待事件结果
     * 调用此方法必须在虚拟线程中
     *
     * @param <T> 请求返回结果类型
     * @param msg 请求消息体
     * @return 返回T结果
     */
    protected <T> T requestEvent(ExecuteSqlMsg msg, DatabaseEvent event) {
        return VertxUtil.requestEvent(event, msg);
    }

    protected <R> Future<Message<R>> futureEvent(ExecuteSqlMsg msg, DatabaseEvent event) {
        return VertxUtil.futureEvent(event, msg);
    }

    protected Date getDate(Row row, String column) {
        LocalDate localDate = row.getLocalDateTime(column).toLocalDate();
        return Date.from(localDate.atStartOfDay(SYSTEM_ZONE).toInstant());
    }

    protected LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(SYSTEM_ZONE).toLocalDateTime();
    }

    protected Buffer buffer(byte[] bytes) {
        if (bytes == null) {
            return Buffer.buffer();
        }

        return Buffer.buffer(bytes);
    }

    /**
     * 点对点发送无返回结果
     *
     * @param address
     * @param message
     */
    protected void sendEvent(Object message, DatabaseEvent address) {
        VertxUtil.sendEvent(address, message);
    }

    /**
     * 点对点发送消息无返回结果
     *
     * @param address
     * @param message
     */
    protected void sendEvent(Object message, String address) {
        VertxUtil.sendEvent(address, message);
    }
}
