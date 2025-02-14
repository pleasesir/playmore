package org.playmore.chat.util;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 检查对象是否为空工具类
 *
 * @Author: zhangpeng
 * @Date: 2023/06/16/16:03
 * @Description:
 */
public class CheckNull {
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof CharSequence) {
            return ((CharSequence) obj).isEmpty();
        } else if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof BitSet) {
            return ((BitSet) obj).isEmpty();
        } else {
            return obj instanceof Map && ((Map<?, ?>) obj).isEmpty();
        }
    }

    public static boolean nonEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 检查多个对象是否有为null
     *
     * @param params
     * @return
     */
    public static boolean hasNull(Object... params) {
        if (Objects.isNull(params) || params.length == 0) {
            return true;
        }
        for (Object param : params) {
            if (isEmpty(param)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNull(Object param) {
        return param == null;
    }
}
