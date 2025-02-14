package org.playmore.chat.cache.component;

import io.netty.util.concurrent.DefaultEventExecutor;
import lombok.Setter;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 抽象定时器缓存类
 *
 * @param <T>
 * @author dahu
 */
public abstract class AbstractTimerCache<K, T extends ICacheVO> extends BasePersistCache<K, T> {
    protected ScheduledExecutorService scheduleService;
    @Setter
    protected int persistInterval = 600;

    public AbstractTimerCache() {
        super();
    }

    public void init(int persistInterval) {
        super.init();
        this.persistInterval = persistInterval;
        scheduleService = new DefaultEventExecutor();
        scheduleService.scheduleWithFixedDelay(new PersistSchedule(),
                new Random().nextInt(persistInterval) + persistInterval, persistInterval, TimeUnit.SECONDS);
    }

    @Override
    protected long getTtl(T t) {
        return Math.max(ttl, persistInterval * 1000 * 2);
    }

    /**
     * 延迟删缓存的判定条件需要比持久化时间间隔更长
     *
     * @return
     */
    @Override
    protected long getDelay() {
        return (persistInterval * 1000L) + 60;
    }

    @Override
    public void destroy() {
        try {
            super.destroy();
            if (scheduleService != null) {
                scheduleService.shutdown();
                scheduleService = null;
            }
        } catch (Exception ex) {
        }
    }

    class PersistSchedule implements Runnable {
        @Override
        public void run() {
            try {
                // 同步到数据库中
                long startNano = System.nanoTime();
                persist(false);
                long costTime = (System.nanoTime() - startNano) / 1000000;
                if (costTime >= persistInterval) {
                }
            } catch (Exception e) {
            }
        }
    }
}
