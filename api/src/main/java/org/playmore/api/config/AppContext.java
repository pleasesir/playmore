package org.playmore.api.config;


import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;
import java.util.Optional;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 23:10
 * @description TODO
 */
public class AppContext implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;

    /**
     * 设置spring上下文  *  * @param applicationContext spring上下文  * @throws BeansException
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        AppContext.context = context;
    }

    /**
     * 获取spring管理的实例
     *
     * @param clazz 要获取的bean的类型
     * @param <T>   要获取的bean的类型
     * @return 容器中的bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    /**
     * 根据名字和类型获取容器中的bean
     *
     * @param name 要获取的bean的名字
     * @param type 要获取的bean的类型
     * @param <T>  要获取的bean的类型
     * @return 容器中的bean
     */
    public static <T> Optional<T> getBean(String name, Class<T> type) {
        if (Objects.isNull(context)) {
            return Optional.empty();
        }
        return Optional.of(context.getBean(name, type));
    }
}
