package org.playmore.api.domain.gm;


import lombok.Getter;
import lombok.Setter;
import org.playmore.api.verticle.BaseVerticle;
import org.playmore.api.verticle.eventbus.ExternalEventConsumer;
import org.playmore.api.verticle.eventbus.event.Address;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-30 0:00
 * @description TODO
 */
public class GmRelation {
    @Getter
    protected Class<? extends BaseVerticle> clazz;
    protected Address[] events;
    protected boolean uniqueAddress;
    @Setter
    private ExternalEventConsumer consumer;

    public GmRelation(Class<? extends BaseVerticle> clazz, ExternalEventConsumer consumer) {
        this.consumer = consumer;
        this.clazz = clazz;
    }

    /**
     * 清除部分信息
     */
    public void clear() {
        events = consumer.addresses();
        uniqueAddress = consumer.subscribe().uniqueAddress();
        consumer = null;
        clazz = null;
    }

    public static boolean hasVarArgsOfType(Method method, Class<?> type) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 检查最后一个参数是否为数组类型
        if (parameterTypes.length > 0 && parameterTypes[parameterTypes.length - 1].isArray()) {
            // 获取数组的组件类型
            Class<?> componentType = parameterTypes[parameterTypes.length - 1].getComponentType();
            // 判断数组的组件类型是否为指定的类型
            return componentType == type;
        }
        return false;
    }

    @Override
    public String toString() {
        return "GmRelation{" +
                "events=" + Arrays.toString(events) +
                ", uniqueAddress=" + uniqueAddress +
                ", consumer=" + consumer +
                '}';
    }
}
