package org.playmore.api.annotation;


import org.playmore.api.verticle.BaseVerticle;

import java.lang.annotation.*;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 23:16
 * @description TODO
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExternalSubscribe {

    /**
     * 绑定的verticle模块
     *
     * @return 对应模块的class
     */
    Class<? extends BaseVerticle> verticle();

    /**
     * 绑定的verticle模块
     *
     * @return 订阅的eventbus事件
     */
    Subscribe subscribe();
}
