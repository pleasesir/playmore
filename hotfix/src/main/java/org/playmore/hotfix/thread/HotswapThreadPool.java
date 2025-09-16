package org.playmore.hotfix.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @ClassName HotswapThreadPool
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/9/16 14:28
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/9/16 14:28
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class HotswapThreadPool {

    private static class InstanceHolder {
        private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread t = new Thread(null, r, "single-hotfix-thread");
            t.setDaemon(true);
            //优先级
            if (Thread.NORM_PRIORITY != t.getPriority()) {
                // 标准优先级
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        });

        private static ScheduledExecutorService getExecutor() {
            return InstanceHolder.executor;
        }
    }

    public static ScheduledExecutorService getExecutor() {
        return InstanceHolder.executor;
    }

}
