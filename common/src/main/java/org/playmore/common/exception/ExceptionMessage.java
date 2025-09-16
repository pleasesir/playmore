package org.playmore.common.exception;

import io.netty.util.internal.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.playmore.common.util.TurPle;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

@Log4j2
public class ExceptionMessage {
    /**
     * 抛出错误信息拼接
     *
     * @param message 错误信息
     * @return 错误信息
     */
    public static TurPle<String, Throwable> throwableSpliceMsg(Object... message) {
        if (null != message) {
            if (message.length == 1 && message[0] instanceof Throwable) {
                return new TurPle<>(null, (Throwable) message[0]);
            }

            StringBuffer sb = new StringBuffer();
            TurPle<String, Throwable> turPle = new TurPle<>();
            try {
                for (Object obj : message) {
                    if (obj == null) {
                        continue;
                    }
                    if (obj instanceof Throwable) {
                        turPle.setB((Throwable) obj);
                        continue;
                    }
                    appendMessage(sb, obj);
                }

                turPle.setA(sb.toString());
                return turPle;
            } catch (Throwable t) {
                log.error("", t);
            }
        }

        return null;
    }

    /**
     * 将数组中的数据拼接为一个字符串
     *
     * @param message
     * @return
     */
    public static String spliceMessage(Object... message) {
        return lambdaSplice(message);
    }

    /**
     * 使用lambda表达式完成遍历操作，试用lambda
     *
     * @param message
     * @return
     */
    private static String lambdaSplice(Object... message) {
        if (null != message) {
            StringBuffer sb = new StringBuffer();
            try {
                Stream.of(message).filter(Objects::nonNull).forEach(obj -> appendMessage(sb, obj));
            } catch (Throwable t) {
                log.error("", t);
            }
            return sb.toString();
        }

        return StringUtil.EMPTY_STRING;
    }

    @SuppressWarnings("unchecked")
    private static void appendMessage(StringBuffer sb, Object obj) {
        if (obj.getClass().isArray()) {
            // 数组
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                appendMessage(sb, Array.get(obj, i));
            }
        } else if (obj instanceof Collection) {
            // 集合
            for (Object o : (Collection<Object>) obj) {
                appendMessage(sb, o);
            }
        } else {
            sb.append(StringUtils.SPACE).append(obj);
        }
    }

    /**
     * 非lamda遍历方式
     *
     * @param message
     * @return
     */
    static String append(Object... message) {
        StringBuffer sb = new StringBuffer();
        if (null != message) {
            for (Object obj : message) {
                appendMessage(sb, obj);
            }
        }
        return sb.toString();
    }
}
