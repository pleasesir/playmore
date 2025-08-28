package org.playmore.api.verticle.manager;


import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.playmore.api.annotation.ExternalSubscribe;
import org.playmore.api.annotation.Subscribe;
import org.playmore.api.config.AppContext;
import org.playmore.api.config.BaseGmCmdProcessor;
import org.playmore.api.verticle.BaseVerticle;
import org.playmore.api.verticle.eventbus.ExternalEventConsumer;
import org.playmore.api.verticle.eventbus.event.Address;
import org.playmore.common.util.CheckNull;
import org.playmore.common.util.ReflectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Method;
import java.util.*;

import static org.playmore.common.constant.VertxContextConst.ANN_METHOD_NAMES;


/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-29 23:50
 * @description TODO
 */
@Slf4j
public class ExternalEventManager implements InitializingBean {
    @Value("${external.event.consumer.classpath:}")
    private String classpath;

    protected final Map<Class<? extends BaseVerticle>, List<ExternalEventConsumer>> consumerMap = new HashMap<>();

    public void addConsumer(Class<? extends BaseVerticle> clazz, ExternalEventConsumer consumers) {
        consumerMap.computeIfAbsent(clazz, k -> new ArrayList<>()).add(consumers);
    }

    public List<ExternalEventConsumer> getConsumer(Class<? extends BaseVerticle> clazz) {
        return consumerMap.get(clazz);
    }

    @Override
    public void afterPropertiesSet() {
        handleExternalSubscribe();
        handleGmCmd();
    }

    /**
     * 处理外部事件订阅
     */
    private void handleExternalSubscribe() {
        if (StringUtils.isEmpty(classpath)) {
            return;
        }

        Set<Class<?>> classSet = ReflectUtils.getClasses(classpath);
        if (CheckNull.isEmpty(classSet)) {
            return;
        }

        MethodAccess methodAccess;
        MethodAccess subAccess = MethodAccess.get(Subscribe.class);
        for (Class<?> clazz : classSet) {
            Method[] methods;
            if (clazz == null || CheckNull.isEmpty(methods = clazz.getMethods())) {
                continue;
            }

            methodAccess = null;
            for (Method method : methods) {
                ExternalSubscribe es = method.getAnnotation(ExternalSubscribe.class);
                if (es == null) {
                    continue;
                }
                if (method.getParameterCount() != 1) {
                    log.error("ExternalSubscribe method must have one parameter class: {}, method: {}",
                            clazz.getSimpleName(), method.getName());
                }
                if (methodAccess == null) {
                    methodAccess = MethodAccess.get(clazz);
                }

                int index = methodAccess.getIndex(method.getName());
                Object instance = AppContext.getBean(clazz);
                addConsumer(es.verticle(), new ExternalEventConsumer(methodAccess, index, instance,
                        getAddress(es.subscribe(), subAccess), es.subscribe()));
            }
        }
    }

    /**
     * 处理gm命令
     */
    private void handleGmCmd() {
        Map<String, BaseGmCmdProcessor> processorMap = AppContext.getContext().getBeansOfType(BaseGmCmdProcessor.class);
        if (CheckNull.nonEmpty(processorMap)) {
            processorMap.values().forEach(processor -> {
                if (CheckNull.isEmpty(processor.getRelationMap())) {
                    return;
                }
                processor.getRelationMap().values().forEach(relation -> {
                    addConsumer(relation.getClazz(), relation.getConsumer());
                    relation.clear();
                });
            });
        }
    }

    /**
     * 转换外部事件消费者
     *
     * @param es       注册外部事件注解信息
     * @param access   方法调用
     * @param instance 方法拥有者实例
     * @param method   方法
     * @return 方法消费者
     */
    public static ExternalEventConsumer transferConsumer(ExternalSubscribe es, MethodAccess access,
                                                         Object instance, Method method) {
        int index = access.getIndex(method.getName());
        return new ExternalEventConsumer(access, index, instance,
                getAddress(es.subscribe(), MethodAccess.get(Subscribe.class)), es.subscribe());
    }

    /**
     * 获取地址
     *
     * @param subscribe 注册外部事件注解信息
     * @param subMethod
     * @return
     */
    public static Address[] getAddress(Subscribe subscribe, MethodAccess subMethod) {
        if (subscribe == null) {
            return null;
        }

        for (String subMethodName : subMethod.getMethodNames()) {
            if (ArrayUtils.contains(ANN_METHOD_NAMES, subMethodName)) {
                continue;
            }
            int subIndex = subMethod.getIndex(subMethodName);
            Object rs = subMethod.invoke(subscribe, subIndex);
            if (!rs.getClass().isArray()) {
                continue;
            }
            Address[] arr = (Address[]) rs;
            if (CheckNull.isEmpty(arr)) {
                continue;
            }
            if ("none".equalsIgnoreCase(arr[0].toString())) {
                continue;
            }
            return arr;
        }

        return null;
    }
}
