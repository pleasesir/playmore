package org.playmore.api.annotation;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmCmd {
    /**
     * gm命令相关配置
     *
     * @return 外部注册事件配置
     */
    ExternalSubscribe external();

    /**
     * gm命令格式
     *
     * @return gm命令格式
     */
    String format() default StringUtils.EMPTY;

    /**
     * gm命令描述
     *
     * @return gm命令描述
     */
    String desc() default StringUtils.EMPTY;
}

