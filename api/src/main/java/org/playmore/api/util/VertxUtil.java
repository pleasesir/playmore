package org.playmore.api.util;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.remoting.ExecutionException;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.TimeoutException;
import org.apache.dubbo.rpc.RpcException;
import org.playmore.api.exception.GameError;
import org.playmore.api.exception.MwException;
import org.playmore.api.verticle.codec.InJvmMessageCodec;
import org.playmore.api.verticle.eventbus.event.Address;
import org.playmore.common.constant.VertxContextConst;
import org.playmore.common.exception.TreasureException;
import org.playmore.common.util.LogUtil;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.playmore.common.constant.VertxContextConst.ACTOR_KEY;
import static org.playmore.common.util.VertxHolder.vertx;


/**
 * vertx工具类
 *
 * @Author: zhangpeng
 * @Date: 2025/02/18/15:28
 * @Description:
 */
public class VertxUtil {

    /**
     * 获取上下文数据
     *
     * @param con context唯一标识key
     * @param <T> key对应的value
     * @return 上下文中存储的数据
     */
    public static <T> T contextData(VertxContextConst con) {
        return Vertx.currentContext().get(con);
    }

    public static <T> T actor() {
        return VertxUtil.contextData(ACTOR_KEY);
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
    @SuppressWarnings("unchecked")
    public static <T> T requestEvent(String address, Object message) {
        DeliveryOptions options = VertxUtil.buildOptions();
        Object rs = VertxUtil.await(vertx.eventBus().request(address, message, options)).body();
        VertxUtil.handleInThrowable(rs);

        return (T) rs;
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
    @SuppressWarnings("unchecked")
    public static <T> T requestEvent(Address address, Object message, DeliveryOptions options) {
        Object rs = VertxUtil.await(vertx.eventBus().request(address.getAddress(), message, options)).body();
        VertxUtil.handleInThrowable(rs);
        return (T) rs;
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
    public static <T> T requestEvent(Address address, Object message) {
        DeliveryOptions options = VertxUtil.buildOptions();
        return VertxUtil.requestEvent(address, message, options);
    }

    /**
     * 处理请求异常
     *
     * @param rs 请求结果
     */
    public static void handleInThrowable(Object rs) {
        if (!(rs instanceof Throwable ext)) {
            return;
        }
        // 获取异常的根本原因，如果没有则使用当前异常
        Throwable throwable;
        Throwable ex = (throwable = ext.getCause()) == null ? ext : throwable;

        // 如果异常是TreasureException类型，则转换并返回相应的错误信息
        switch (ex) {
            case TreasureException mwe -> {
                // 记录警告日志
                LogUtil.warn(ex.toString(), ex);
                // 创建并返回错误基础信息
                throw mwe;
                // 创建并返回错误基础信息
            }
            case RemotingException ignored -> {
                // 记录错误日志
                LogUtil.error(ex.getMessage(), ex);
                GameError error;
                // 根据异常类型确定具体的错误
                if (ex instanceof ExecutionException) {
                    error = GameError.INVOKER_FAIL;
                } else if (ex instanceof TimeoutException) {
                    error = GameError.INVOKER_TIMEOUT;
                } else {
                    error = GameError.SERVER_CONNECT_EXCEPTION;
                }

                throw new MwException(error.getCode());
            }
            case RpcException e -> {
                // 记录错误日志
                LogUtil.error(ex.getMessage(), ex);
                GameError error = GameError.SERVER_CONNECT_FAIL;
                // 判断RPC异常的具体类型，如果是特定的FORBIDDEN异常，则返回不同的错误代码
                if (e.getCode() == RpcException.FORBIDDEN_EXCEPTION) {
                    error = GameError.SERVER_NOT_FOUND;
                }

                throw new MwException(error.getCode());
            }
            default -> {
                // 对于未处理的其他类型异常，记录错误日志并返回未知错误代码
                LogUtil.error(" Not Hand  Exception -->" + ex.getMessage(), ex);
                // 创建并返回未知错误基础信息
                throw new MwException(GameError.UNKNOWN_ERROR.getCode());
            }
        }
    }

    /**
     * vertx事件总线发送消息, 并等待事件结果
     * 调用此方法必须在虚拟线程中
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param <T>     请求返回结果类型
     * @param params  额外特殊地址参数
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    public static <T> T requestEvent(Address address, Object message, DeliveryOptions options, String... params) {
        String rqAddress = VertxUtil.buildAddress(address.getAddress(), params);
        Object rs = VertxUtil.await(vertx.eventBus().request(rqAddress, message, options)).body();
        VertxUtil.handleInThrowable(rs);
        return (T) rs;
    }

    /**
     * vertx事件总线发送消息, 并等待事件结果
     * 调用此方法必须在虚拟线程中
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param <T>     请求返回结果类型
     * @param params  额外特殊地址参数
     * @return 执行结果
     */
    public static <T> T requestEvent(Address address, Object message, String... params) {
        DeliveryOptions options = VertxUtil.buildOptions();
        return VertxUtil.requestEvent(address, message, options, params);
    }

    /**
     * 订阅发布消息无返回结果
     *
     * @param address eventbus请求地址
     * @param message eventbus请求消息体
     */
    public static void publishEvent(Address address, Object message, String... params) {
        DeliveryOptions options = VertxUtil.buildOptions();
        String adExt = VertxUtil.buildAddress(address.getAddress(), params);
        vertx.eventBus().publish(adExt, message, options);
    }

    /**
     * 订阅发布消息无返回结果
     *
     * @param address eventbus请求地址
     * @param message eventbus请求消息体
     */
    public static void publishEvent(String address, Object message) {
        DeliveryOptions options = VertxUtil.buildOptions();
        vertx.eventBus().publish(address, message, options);
    }

    /**
     * 点对点发送无返回结果
     *
     * @param address eventbus请求地址
     * @param message eventbus请求消息体
     */
    public static void sendEvent(Address address, Object message) {
        DeliveryOptions options = VertxUtil.buildOptions();
        vertx.eventBus().send(address.getAddress(), message, options);
    }

    /**
     * 点对点发送无返回结果
     *
     * @param address eventbus请求地址
     * @param message eventbus请求消息体
     */
    public static void sendEvent(Address address, Object message, String... params) {
        String realAddress = VertxUtil.buildAddress(address.getAddress(), params);
        VertxUtil.sendEvent(realAddress, message);
    }

    /**
     * 点对点发送消息无返回结果
     *
     * @param address eventbus请求地址
     * @param message eventbus请求消息体
     */
    public static void sendEvent(String address, Object message) {
        DeliveryOptions options = VertxUtil.buildOptions();
        vertx.eventBus().send(address, message, options);
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
    public static <T> Future<Message<T>> futureEvent(String address, Object message) {
        DeliveryOptions options = VertxUtil.buildOptions();
        return vertx.eventBus().request(address, message, options);
    }

    /**
     * vertx事件总线发送消息
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param <T>     请求返回结果类型
     * @param options 自定义请求参数
     * @return 返回未来T结果
     */
    public static <T> Future<Message<T>> futureEvent(Address address, Object message, DeliveryOptions options,
                                                     String... params) {
        String ext = VertxUtil.buildAddress(address.getAddress(), params);
        return vertx.eventBus().request(ext, message, options);
    }

    /**
     * vertx事件总线发送消息
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param <T>     请求返回结果类型
     * @return 返回未来T结果
     */
    public static <T> Future<Message<T>> futureEvent(Address address, Object message) {
        DeliveryOptions options = VertxUtil.buildOptions();
        return vertx.eventBus().request(address.getAddress(), message, options);
    }

    public static <T> Future<Message<T>> futureEvent(Address address, Object message, String... params) {
        String ext = VertxUtil.buildAddress(address.getAddress(), params);
        return vertx.eventBus().request(ext, message, VertxUtil.buildOptions());
    }

    /**
     * 构建发布eventbus事件选项
     *
     * @return DeliveryOptions
     */
    public static DeliveryOptions buildOptions() {
        DeliveryOptions options = new DeliveryOptions().setSendTimeout(3000);
        if (!vertx.isClustered()) {
            // 如果是非集群模式, 直接使用内存引用
            options.setCodecName(InJvmMessageCodec.CODEC_NAME);
        }

        return options;
    }

    /**
     * 添加消息头的DeliveryOptions
     *
     * @param headers 消息头
     * @return DeliveryOptions
     */
    public static DeliveryOptions buildOptions(MultiMap headers) {
        DeliveryOptions options = new DeliveryOptions().setSendTimeout(3000).setHeaders(headers);
        if (!vertx.isClustered()) {
            // 如果是非集群模式, 直接使用内存引用
            options.setCodecName(InJvmMessageCodec.CODEC_NAME);
        }

        return options;
    }

    /**
     * 构建eventbus地址
     *
     * @param address 地址
     * @param params  额外参数
     * @return eventbus地址
     */
    public static String buildAddress(Address address, String... params) {
        if (params == null || params.length == 0) {
            return address.getAddress();
        }
        if (params.length == 1 && StringUtils.isEmpty(params[0])) {
            return address.getAddress();
        }

        String ext = Arrays.stream(params).map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.joining("_"));
        return String.join("_", address.getAddress(), ext);
    }


    /**
     * 拼接eventbus地址
     *
     * @param address 请求地址
     * @param params  请求参数
     * @return 返回拼接地址
     */
    public static String buildAddress(String address, String... params) {
        if (params == null || params.length == 0) {
            return address;
        }
        if (params.length == 1 && StringUtils.isEmpty(params[0])) {
            return address;
        }

        String ext = Arrays.stream(params).map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.joining("_"));
        return String.join("_", address, ext);
    }

    /**
     * 虚拟线程等待结果返回
     *
     * @param future 等待的未来结构
     * @param <T>    未来结构内容
     * @return 未来结果
     * @throws TreasureException 运行时异常
     */
    public static <T> T await(Future<T> future) throws TreasureException {
        return VertxUtil.await(future, -1L, null);
    }

    /**
     * Like {@link #await(Future)} but with a timeout.
     *
     * @param timeout the timeout
     * @param unit    the timeout unit
     * @return the result
     * @throws TreasureException     运行时异常
     * @throws IllegalStateException when called from a vertx event-loop or worker thread
     */
    public static <T> T await(Future<T> future, long timeout, TimeUnit unit) throws TreasureException {
        if (timeout >= 0L && unit == null) {
            throw new NullPointerException();
        }
        io.vertx.core.impl.WorkerExecutor executor = io.vertx.core.impl.WorkerExecutor.unwrapWorkerExecutor();
        CountDownLatch latch;
        if (executor != null) {
            latch = executor.suspend(cont -> future.onComplete(ar -> cont.resume()));
        } else {
            latch = new CountDownLatch(1);
            future.onComplete(ar -> latch.countDown());
        }
        if (latch != null) {
            try {
                if (timeout >= 0) {
                    Objects.requireNonNull(unit);
                    if (!latch.await(timeout, unit)) {
                        throw new TreasureException(15, "等待vert.x future超时");
                    }
                } else {
                    latch.await();
                }
            } catch (InterruptedException e) {
                Utils.throwAsUnchecked(e);
                return null;
            }
        }
        if (future.succeeded()) {
            return future.result();
        } else if (future.failed()) {
            Utils.throwAsUnchecked(future.cause());
            return null;
        } else {
            Utils.throwAsUnchecked(new InterruptedException("Context closed"));
            return null;
        }
    }
}
