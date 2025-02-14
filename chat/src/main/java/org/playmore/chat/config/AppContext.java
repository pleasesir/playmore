package org.playmore.chat.config;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * @program: account
 * @description:
 * @author: zhou jie
 * @create: 2019-08-01 19:02
 */
@Component
@Lazy(false)
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
     * @param clazz
     * @param <T>
     * @return
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