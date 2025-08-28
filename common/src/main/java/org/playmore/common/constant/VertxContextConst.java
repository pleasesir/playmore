package org.playmore.common.constant;

/**
 * vertx 上下文常量
 *
 * @Author: zhangpeng
 * @Date: 2025/03/14/15:56
 * @Description:
 */
public enum VertxContextConst {
    ACTOR_KEY(),
    ;

    /**
     * 注解不需要检测的方法
     */
    public static final String[] ANN_METHOD_NAMES = new String[]{"equals", "toString", "hashCode", "annotationType"};
}
