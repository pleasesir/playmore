package org.playmore.api.handler;


import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import lombok.Getter;
import lombok.Setter;
import org.playmore.api.domain.PlayerEntity;
import org.playmore.api.util.VertxUtil;
import org.playmore.api.verticle.eventbus.event.Address;
import org.playmore.common.msg.BaseRpcMsg;
import org.playmore.common.util.ExceptionUtil;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.MdcUtil;
import org.playmore.common.util.NumberUtil;
import org.slf4j.MDC;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 22:55
 * @description TODO
 */
public abstract class BaseRpcHandler<M extends BaseRpcMsg> implements Runnable {
    /**
     * 请求日志追踪id
     */
    protected String traceId;
    /**
     * handler创建时间
     */
    protected long startTime;
    @Setter
    protected M packet;
    protected Throwable ex;
    protected String req;
    @Setter
    protected Object rqMsg;
    @Getter
    protected Object rsMsg;
    protected int rqCmd;
    protected int rsCmd;
    @Setter
    protected PlayerEntity playerEntity;

    public BaseRpcHandler() {
        startTime = System.currentTimeMillis();
    }

    /**
     * handler 执行
     */
    @Override
    public void run() {
        try {
            try {
                actionBefore();
            } catch (Throwable t) {
                this.ex = t;
            }

            if (ex == null) {
                try {
                    action();
                } catch (Throwable t) {
                    ex = t;
                }
            }

            if (ex != null) {
                try {
                    handleInvokeThrowable(ex);
                } catch (Throwable t) {
                    ExceptionUtil.analysisThrowable(getClass(), t);
                }
            }

            try {
                onCompletion();
            } catch (Throwable t) {
                ExceptionUtil.analysisThrowable(getClass(), t);
            }
        } finally {
            MDC.clear();
        }
    }

    /**
     * 逻辑执行前
     */
    protected void actionBefore() {
        traceId = MdcUtil.getTraceId();
        MDC.put(MdcUtil.TRACE_ID, traceId);
        req = LogUtil.getReq(this.getClass());
    }

    protected void onCompletion() {
        long roleId = playerEntity == null ? 0 : playerEntity.getRoleId();
        LogUtil.traceMessage(rsMsg, roleId);
        long costMills;
        if ((costMills = System.currentTimeMillis() - startTime) >= NumberUtil.HALF_OF_HUNDRED) {
            LogUtil.warn(this.getClass(), "roleId:", roleId, ", costMills:", costMills, "ms");
        }
    }

    public void initCmd(int rqCmd, int rsCmd) {
        this.rqCmd = rqCmd;
        this.rsCmd = rsCmd;
    }

    /**
     * vertx事件总线发送消息, 不等待事件结果
     * 调用此方法必须在虚拟线程中
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param <T>     请求返回结果类型
     * @return 返回未来T结果
     */
    protected <T> Future<Message<T>> futureEvent(String address, Object message) {
        return VertxUtil.futureEvent(address, message);
    }

    /**
     * vertx事件总线发送消息, 并等待事件结果
     * 调用此方法必须在虚拟线程中
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param <T>     请求返回结果类型
     * @return 返回未来T结果
     */
    protected <T> Future<Message<T>> futureEvent(Address address, Object message) {
        return VertxUtil.futureEvent(address, message);
    }

    /**
     * vertx事件总线发送消息, 并等待事件结果
     * 调用此方法必须在虚拟线程中
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param <T>     请求返回结果类型
     * @return 返回T结果
     */
    protected <T> T requestEvent(Address address, Object message) {
        return VertxUtil.requestEvent(address, message);
    }

    /**
     * vertx事件总线发送消息, 并等待事件结果
     * 调用此方法必须在虚拟线程中
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param options 请求消息体参数
     * @param <T>     请求返回结果类型
     * @return 返回T结果
     */
    protected <T> T requestEvent(Address address, Object message, DeliveryOptions options) {
        return VertxUtil.requestEvent(address, message, options);
    }

    /**
     * vertx事件总线发送消息, 并等待事件结果
     * 调用此方法必须在虚拟线程中
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param <T>     请求返回结果类型
     * @return 返回T结果
     */
    protected <T> T requestEvent(String address, Object message) {
        return VertxUtil.requestEvent(address, message);
    }

    /**
     * 执行结果
     *
     * @throws Exception 执行异常
     */
    protected abstract void action() throws Throwable;

    /**
     * 处理报错
     *
     * @param t 抛出异常
     */
    protected abstract void handleInvokeThrowable(Throwable t);
}
