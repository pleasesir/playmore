package org.playmore.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newThreadPerTaskExecutor;

/**
 * @ClassName ThreadUtil
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:51
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:51
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public final class ThreadUtil {

    public static ExecutorService createVirtualThreadFactory(String name) {
        ThreadFactory factory = Thread.ofVirtual()
                .uncaughtExceptionHandler((t, e) ->
                        LogUtil.error("", e))
                .name(name, 0).factory();
        return newThreadPerTaskExecutor(factory);
    }

    public static void sleep(TimeUnit timeUnit, int timeout) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            LogUtil.error("", e);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LogUtil.error("", e);
        }
    }

    public static void shutdownThreadPool(ExecutorService executor, int time, TimeUnit timeUnit) {
        executor.shutdown();
        long mills = timeUnit.toMillis(time);
        long startMills = System.currentTimeMillis();
        while (System.currentTimeMillis() - startMills < mills) {
            try {
                if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    return;
                }
            } catch (Throwable ex) {
                LogUtil.error("ThreadPoolManager shutdown executor has error : {}", ex);
            }
        }
        executor.shutdownNow();
    }
}
