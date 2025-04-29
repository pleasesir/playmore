package org.playmore.common.util;


import org.playmore.common.exception.TreasureException;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 22:59
 * @description TODO
 */
public class ExceptionUtil {
    public static void analysisThrowable(Class<?> clazz, Throwable t) {
        Throwable throwable;
        Throwable ex = (throwable = t.getCause()) == null ? t : throwable;
        if (ex instanceof TreasureException) {
            LogUtil.warn(ex.getMessage(), ex);
        } else {
            LogUtil.error(clazz.getSimpleName() + " Not Hand  Exception -->" + ex.getMessage(), ex);
        }
    }
}
