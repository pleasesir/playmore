package org.playmore.api.verticle;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.playmore.api.annotation.Subscribe;
import org.playmore.api.config.AppContext;
import org.playmore.api.util.VertxUtil;
import org.playmore.api.verticle.eventbus.ExternalEventConsumer;
import org.playmore.api.verticle.eventbus.event.Address;
import org.playmore.api.verticle.manager.ExternalEventManager;
import org.playmore.common.util.CheckNull;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.NumberUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.playmore.api.util.VertxUtil.buildOptions;

/**
 * @Author: zhangpeng
 * @Date: 2025/02/13/16:57
 * @Description:
 */
@Slf4j
public class BaseVerticle extends AbstractVerticle {

    /**
     * method-map
     */
    private HashMap<Address, Integer> methodMap;
    /**
     * method-access
     */
    private MethodAccess methodAccess;
    /**
     * 注解不需要检测的方法
     */
    public static final String[] ANN_METHOD_NAMES = new String[]{"equals", "toString", "hashCode", "annotationType"};

    @Override
    public void start() throws Exception {
        register();
        registerExternal();
    }

    /**
     * 注册外部事件总线消费者
     */
    private void registerExternal() {
        if (AppContext.getContext() == null) {
            return;
        }
        ExternalEventManager manager = AppContext.getBean(ExternalEventManager.class);
        List<ExternalEventConsumer> consumers = manager.getConsumer(this.getClass());
        registerDetail(consumers);
    }

    /**
     * 注册外部事件总线消费者具体逻辑
     *
     * @param consumers
     */
    private void registerDetail(List<ExternalEventConsumer> consumers) {
        if (CheckNull.nonEmpty(consumers)) {
            consumers.forEach(consumer -> {
                if (CheckNull.isEmpty(consumer.addresses())) {
                    return;
                }

                for (Address address : consumer.addresses()) {
                    register(address, consumer.subscribe().uniqueAddress(), consumer::invoke);
                }
            });
        }
    }

    /**
     * 注册eventbus消费者
     */
    protected void register() {
        Method[] methodArr = this.getClass().getDeclaredMethods();
        methodAccess = MethodAccess.get(this.getClass());
        MethodAccess subMethod = MethodAccess.get(Subscribe.class);
        for (Method method : methodArr) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe == null) {
                continue;
            }

            boolean uniqueAddress = subscribe.uniqueAddress();
            for (String subMethodName : subMethod.getMethodNames()) {
                if (ArrayUtils.contains(ANN_METHOD_NAMES, subMethodName)) {
                    continue;
                }
                int subIndex = subMethod.getIndex(subMethodName);
                Object rs = subMethod.invoke(subscribe, subIndex);
                if (rs.getClass().isArray()) {
                    Stream.of((Object[]) rs).forEach(obj -> handleRegister(obj, method, uniqueAddress));
                }
            }
        }
    }

    private void handleRegister(Object rs, Method method, boolean needUniqueAddress) {
        if (!(rs instanceof Address address)) {
            return;
        }
        if ("none".equalsIgnoreCase(address.toString())) {
            return;
        }
        if (methodMap == null) {
            methodMap = new HashMap<>(8);
        }

        methodMap.compute(address, (k, v) -> {
            if (v == null) {
                return methodAccess.getIndex(method.getName());
            } else {
                log.error("methodAccessInfo is not null, clazz:{}, address: {}",
                        this.getClass().getSimpleName(), address);
                return v;
            }
        });

        register(address, needUniqueAddress, (message) -> {
            Integer index = methodMap.get(address);
            if (index == null) {
                log.error("methodAccessInfo is null, address: {}", address);
                return null;
            }
            return methodAccess.invoke(this, index, message);
        });
    }

    /**
     * vertx事件总线发送消息, 不返回任何结果
     * 点对面发送事件
     *
     * @param address eventbus地址
     * @param message 请求消息体
     */
    protected void publishEvent(String address, Object message) {
        VertxUtil.publishEvent(address, message);
    }


    /**
     * vertx事件总线发送消息, 不返回任何结果
     * 点对面 发布事件
     *
     * @param address eventbus地址
     * @param message 请求消息体
     */
    protected void publishEvent(Address address, Object message, String... ads) {
        String realAddress = VertxUtil.buildAddress(address.getAddress(), ads);
        VertxUtil.publishEvent(realAddress, message);
    }

    /**
     * vertx事件总线发送消息, 不返回任何结果
     * 点对点发送事件
     *
     * @param address eventbus地址
     * @param message 请求消息体
     */
    protected void sendEvent(String address, Object message) {
        VertxUtil.sendEvent(address, message);
    }


    /**
     * vertx事件总线发送消息, 不返回任何结果
     * 点对点发送事件
     *
     * @param address eventbus地址
     * @param message 请求消息体
     */
    protected void sendEvent(Address address, Object message, String... ads) {
        String realAddress = VertxUtil.buildAddress(address.getAddress(), ads);
        VertxUtil.sendEvent(realAddress, message);
    }

    /**
     * vertx事件总线发送消息, 返回事件未来结果
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
     * vertx事件总线发送消息, 返回事件未来结果
     *
     * @param address eventbus地址
     * @param message 请求消息体
     * @param <T>     请求返回结果类型
     * @return 返回未来T结果
     */
    protected <T> Future<Message<T>> futureEvent(Address address, Object message) {
        return VertxUtil.futureEvent(address.getAddress(), message);
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
     * @param <T>     请求返回结果类型
     * @return 返回T结果
     */
    protected <T> T requestEvent(String address, Object message) {
        return VertxUtil.requestEvent(address, message);
    }

    /**
     * 注册消费者
     *
     * @param address  eventbus注册地址
     * @param consumer 接收请求的处理
     * @param <T>      返回的请求结果类型
     */
    public <P, T> void register(Address address, boolean needUniqueAddress, Function<P, T> consumer) {
        String realAddress = needUniqueAddress ? VertxUtil.buildAddress(address, uniqueAddress()) : address.getAddress();
        registerFunction(realAddress, consumer);
    }

    /**
     * 注册cast类消费者
     *
     * @param address           消费注册地址
     * @param needUniqueAddress 是否需要唯一地址
     * @param consumer          消费者
     */
    public <T> void register(Address address, boolean needUniqueAddress, Consumer<T> consumer) {
        String realAddress = needUniqueAddress ? VertxUtil.buildAddress(address, uniqueAddress()) : address.getAddress();
        registerConsumer(realAddress, consumer);
    }

    /**
     * 注册callback消费者
     *
     * @param address           消费注册地址
     * @param needUniqueAddress 是否需要唯一地址
     * @param consumer          消费者
     * @param <T>
     */
    public <P, T> void register(String address, boolean needUniqueAddress, Function<P, T> consumer) {
        String realAddress = needUniqueAddress ? VertxUtil.buildAddress(address, uniqueAddress()) : address;
        registerFunction(realAddress, consumer);
    }

    /**
     * 注册callback消费者
     *
     * @param realAddress
     * @param consumer
     * @param <P>
     * @param <T>
     */
    private <P, T> void registerFunction(String realAddress, Function<P, T> consumer) {
        vertx.eventBus().<P>localConsumer(realAddress, event -> {
            long startTime = System.currentTimeMillis();
            P msg = event.body();
            try {
                T rs = consumer.apply(msg);
                event.reply(rs, buildOptions());
            } catch (Throwable tx) {
                LogUtil.error("vertx eventbus, ads: ", realAddress, "msg: ", msg, ", consumer: ", consumer, ", error: ", tx);
                event.reply(tx);
            }

            long costMills = System.currentTimeMillis() - startTime;
            if (costMills >= NumberUtil.HALF_OF_HUNDRED) {
                LogUtil.warn("vertx eventbus, ads: ", realAddress, "msg: ", msg, ", consumer: ", consumer, ", costMills: ", costMills, "ms");
            }
        });
    }

    /**
     * 注册cast事件消费者
     *
     * @param realAddress
     * @param consumer
     * @param <T>
     */
    private <T> void registerConsumer(String realAddress, Consumer<T> consumer) {
        vertx.eventBus().<T>localConsumer(realAddress, event -> {
            long startTime = System.currentTimeMillis();
            T msg = event.body();
            try {
                consumer.accept(msg);
                event.reply(null);
            } catch (Throwable tx) {
                LogUtil.error("vertx eventbus, ads: ", realAddress, "msg: ", msg, ", consumer: ", consumer, ", error: ", tx);
            }

            long costMills = System.currentTimeMillis() - startTime;
            if (costMills >= NumberUtil.HALF_OF_HUNDRED) {
                LogUtil.warn("vertx eventbus, ads: ", realAddress, "msg: ", msg, ", consumer: ", consumer, ", costMills: ", costMills, "ms");
            }
        });
    }

    /**
     * 注册cast类消费者
     *
     * @param address
     * @param needUniqueAddress
     * @param consumer
     */
    public <T> void register(String address, boolean needUniqueAddress, Consumer<T> consumer) {
        String realAddress = needUniqueAddress ? VertxUtil.buildAddress(address, uniqueAddress()) : address;
        registerConsumer(realAddress, consumer);
    }

    /**
     * 特殊的eventbus拼接地址
     *
     * @return 特殊字符串地址
     */
    protected String uniqueAddress() {
        return StringUtils.EMPTY;
    }

    /**
     * 读取配置文件
     *
     * @return
     * @throws IOException
     */
    protected Properties readProperties(String fileName) throws IOException {
        Resource resource;
        InputStream stream;
        try {
            resource = new FileSystemResource(fileName);
            stream = resource.getInputStream();
        } catch (Exception ex) {
            resource = new ClassPathResource(fileName);
            stream = resource.getInputStream();
        }

        final InputStream input = stream;
        try (input) {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        }
    }
}
