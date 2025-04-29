package org.playmore.common.util;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.playmore.common.exception.ExceptionMessage;
import org.slf4j.MDC;

/**
 * @Author: zhangpeng
 * @Date: 2025/03/03/18:52
 * @Description:
 */
@Slf4j
public class LogUtil {
    public static final Logger COMMON_LOGGER;
    public static final Logger MESSAGE_LOGGER;
    public static final Logger WARN_LOGGER;
    public static final Logger ERROR_LOGGER;
    public static final Logger CACHE_LOGGER;
    private static final Logger SAVE_LOGGER;
    private static final Logger JSON_LOGGER;
    private static final Logger THINKING_DATA_LOGGER;

    private static final String CURRENT_ERROR = "当前报错: ";

    static {
        LoggerContext loggerContext = LogManager.getContext();
        SAVE_LOGGER = loggerContext.getLogger("SAVE");
        COMMON_LOGGER = loggerContext.getLogger("COMMON");
        MESSAGE_LOGGER = loggerContext.getLogger("MESSAGE");
        WARN_LOGGER = loggerContext.getLogger("WARN");
        ERROR_LOGGER = loggerContext.getLogger("ERROR");
        CACHE_LOGGER = loggerContext.getLogger("CACHE");
        JSON_LOGGER = loggerContext.getLogger("JSON");
        THINKING_DATA_LOGGER = loggerContext.getLogger("THINKING-DATA");
    }

    /**
     * 落下埋点日志
     *
     * @param log 日志信息
     */
    public static void logDataTracking(String log) {
        JSON_LOGGER.info(log);
    }

    /**
     * 设置指定logger的级别。
     *
     * @param name  Logger的名称，对应要设置日志级别的Logger的标识符。
     * @param level 要设置的日志级别，以整数形式表示。具体的级别值需要与Logback或Log4j等日志框架支持的级别值对应。
     */
    public static Logger setLevel(String name, int level) {
        return null;
    }

    public static void thinkingData(String log) {
        THINKING_DATA_LOGGER.info(log);
    }

    public static void start(Object message) {
        COMMON_LOGGER.info("[start] {} {}", getClassPath(), message);
    }

    public static void stop(Object message) {
        COMMON_LOGGER.info("[stop] {} {}", getClassPath(), message);
    }

    public static void save(Object message) {
        SAVE_LOGGER.info("[save] {} {}", getClassPath(), message);
    }

    public static void debug(Object... message) {
        COMMON_LOGGER.debug("[debug] {} {}", getClassPath(), ExceptionMessage.spliceMessage(message));
    }

    /**
     * 将协议日志打印成短格式的日志
     *
     * @param obj
     * @return
     */
    public static Object obj2ShortStr(Object obj) {
        if (!FileUtil.isWindows() && (obj instanceof GeneratedMessage)) {
            return TextFormat.shortDebugString((MessageOrBuilder) obj);
        }
        return obj;
    }

    public static void common(Object... message) {
        try {
            COMMON_LOGGER.info("{} {}", getClassPath(), ExceptionMessage.spliceMessage(message));
        } catch (Exception e) {
            log.info(ExceptionMessage.spliceMessage(message));
            log.error("", e);
        }
    }

    public static void handleTaskLogBegin(String clazz) {
        try {
            MESSAGE_LOGGER.info("[message] {} task: {} , traceId: {}  execute...", getClassPath(), clazz,
                    MDC.get(MdcUtil.TRACE_ID));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private static final String MSG_FORMAT = "[message] {}, task: {}, traceId: {},  execute complete";

    public static void handleTaskLogEnd(String clazz, String traceId) {
        try {
            MESSAGE_LOGGER.info(MSG_FORMAT, getClassPath(), clazz, traceId);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static void message(Object message, Long roleId) {
        try {
            MESSAGE_LOGGER.info("[message] {}, roleId: {}, {}", getClassPath(), roleId, obj2ShortStr(message));
        } catch (Exception e) {
            log.info(ExceptionMessage.spliceMessage(message));
            log.error("", e);
        }
    }

    public static void traceMessage(Object msg, Long roleId, long mapId, long seasonMapId) {
        try {
            MESSAGE_LOGGER.info("[message] {} traceId: {}, roleId: {}, mapId: {}, seasonMapId: {}, {}",
                    getClassPath(), MDC.get(MdcUtil.TRACE_ID), roleId, +mapId, +seasonMapId, obj2ShortStr(msg));
        } catch (Exception e) {
            log.info(ExceptionMessage.spliceMessage(msg));
            log.error("", e);
        }
    }

    public static void traceMessage(Object message, Long roleId) {
        try {
            MESSAGE_LOGGER.info("[message] {} traceId: {}, roleId: {}, {}",
                    getClassPath(), MDC.get(MdcUtil.TRACE_ID), roleId, obj2ShortStr(message));
        } catch (Exception e) {
            log.info(ExceptionMessage.spliceMessage(message));
            log.error("", e);
        }
    }

    public static void messageDebug(Object... message) {
        try {
            MESSAGE_LOGGER.debug("{} {}", getClassPath(), ExceptionMessage.spliceMessage(message));
        } catch (Exception e) {
            log.info(ExceptionMessage.spliceMessage(message));
            log.error("", e);
        }
    }

    public static void message(Object... message) {
        try {
            MESSAGE_LOGGER.info("{} {}", getClassPath(), ExceptionMessage.spliceMessage(message));
        } catch (Exception e) {
            log.info(ExceptionMessage.spliceMessage(message));
            log.error("", e);
        }
    }

    public static void warn(Object... message) {
        if (CheckNull.isEmpty(message)) {
            return;
        }

        TurPle<String, Throwable> logMsg = ExceptionMessage.throwableSpliceMsg(message);
        if (logMsg == null) {
            return;
        }

        if (logMsg.getB() != null) {
            WARN_LOGGER.warn("{} {}", getClassPath(), (logMsg.getA() == null ? CURRENT_ERROR : logMsg.getA()),
                    logMsg.getB());
        } else {
            WARN_LOGGER.warn("{} {}", getClassPath(), logMsg.getA());
        }
    }

    public static void error(Object... message) {
        if (CheckNull.isEmpty(message)) {
            return;
        }

        TurPle<String, Throwable> logMsg = ExceptionMessage.throwableSpliceMsg(message);
        if (logMsg == null) {
            return;
        }

        if (logMsg.getB() != null) {
            ERROR_LOGGER.error(getClassPath() + (logMsg.getA() == null ? CURRENT_ERROR : logMsg.getA()),
                    logMsg.getB());
        } else {
            ERROR_LOGGER.error(logMsg.getA());
        }
    }

    public static String getClassPath() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement ele = stackTraceElements[3];
        String className = ele.getClassName();
        return getSimpleClassName(className) + "." + ele.getMethodName() + "():" + ele.getLineNumber() + " - ";
    }

    public static String getClassPath(int stackIndex) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length <= stackIndex) {
            stackIndex = 3;
        }
        StackTraceElement ele = stackTraceElements[stackIndex];
        return getSimpleClassName(ele.getClassName()) + "." + ele.getMethodName() + "():" + ele.getLineNumber() + " - ";
    }

    public static String getSimpleClassName(String fileName) {
        if (fileName == null) {
            return "null";
        }
        int index = fileName.lastIndexOf(".");
        if (index > 0 && ++index < fileName.length()) {
            return fileName.substring(index);
        }
        return fileName;
    }

    public static final String DOLLAR = "$";

    /**
     * 获取当前线程的调用栈信息，用于定位代码执行路径。
     * 该方法主要为了获取调用者的信息，通过遍历栈轨迹元素，找到合适的栈帧作为调用者信息返回。
     * 特别地，忽略了初始化方法和内部类方法，以获得更准确的调用者信息。
     *
     * @return 当前线程的调用栈信息字符串，如果栈信息为空，则返回空字符串。
     */
    public static String getReq(Class<?> clazz) {
        try {
            // 获取当前线程的栈轨迹元素数组
            StackTraceElement[] steArr = Thread.currentThread().getStackTrace();
            // 检查栈轨迹元素数组是否为空
            if (CheckNull.isEmpty(steArr)) {
                return StringUtils.EMPTY;
            }
            // 初始化遍历的起始位置
            int length = 4;
            // 遍历栈轨迹元素数组，寻找合适的调用者信息
            while (length < steArr.length) {
                StackTraceElement ele = steArr[length++];
                // 如果当前元素为空，跳过
                if (ele == null) {
                    continue;
                }
                // 如果当前方法名包含"init"，即为初始化方法，跳过
                if (ele.getLineNumber() <= 0 || ele.getMethodName().contains("init")) {
                    continue;
                }
                String simpleClassName = LogUtil.getSimpleClassName(ele.getClassName());
                // 如果当前方法名包含"$"，即为内部类方法，处理并返回调用者信息
                if (ele.getMethodName().contains(DOLLAR)) {
                    String methodName = ele.getMethodName();
                    int begin = methodName.indexOf(DOLLAR);
                    int end = methodName.lastIndexOf(DOLLAR);
                    methodName = methodName.substring(begin + 1, end);
                    // 格式化并返回调用者信息
                    return simpleClassName + "." + methodName + "():" + ele.getLineNumber();
                }
                // 如果不是内部类方法，直接返回调用者信息
                return simpleClassName + "." + ele.getMethodName() + "():" + ele.getLineNumber()
                        + getWithoutDollar(clazz);
            }
        } catch (Throwable t) {
            LogUtil.error("", t);
        }

        // 如果遍历结束后仍未找到合适的调用者信息，返回空字符串
        return StringUtils.EMPTY;
    }

    private static String getWithoutDollar(Class<?> clazz) {
        String className = clazz.getName();
        return " - " + LogUtil.getSimpleClassName(className);
    }
}
