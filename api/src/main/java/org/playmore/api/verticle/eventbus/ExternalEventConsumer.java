package org.playmore.api.verticle.eventbus;


import com.esotericsoftware.reflectasm.MethodAccess;
import org.playmore.api.annotation.Subscribe;
import org.playmore.api.verticle.eventbus.event.Address;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-29 23:51
 * @description TODO
 */
public record ExternalEventConsumer(MethodAccess access, int methodIndex, Object instance,
                                    Address[] addresses, Subscribe subscribe) {

    /**
     * 执行事件消费
     *
     * @param msg 传入消息
     * @return 执行逻辑结果
     */
    public Object invoke(Object msg) {
        return access.invoke(instance, methodIndex, msg);
    }
}
