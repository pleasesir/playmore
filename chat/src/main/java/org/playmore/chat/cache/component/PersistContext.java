package org.playmore.chat.cache.component;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.springframework.util.comparator.Comparators;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-09-14 15:43
 */
public class PersistContext {
    private final AtomicInteger insertCount;
    private final AtomicInteger updateCount;
    private final AtomicInteger deleteCount;
    private final int ttl;
    private final int delay;
    private final long startTime;
    private final Map<Thread, Long> serCost = new ConcurrentHashMap<>();
    private final Map<Thread, Long> persistTimeMap = new ConcurrentHashMap<>();
    private final String cacheName;
    private final Map<Class<?>, List<Model<?>>> saveMap = new ConcurrentHashMap<>();


    public PersistContext(String cacheName, int ttl, int delay) {
        this.cacheName = cacheName;
        this.ttl = ttl;
        this.delay = delay;
        insertCount = new AtomicInteger();
        updateCount = new AtomicInteger();
        deleteCount = new AtomicInteger();
        startTime = System.currentTimeMillis();
    }

    public AtomicInteger getInsertCount() {
        return insertCount;
    }

    public AtomicInteger getUpdateCount() {
        return updateCount;
    }

    public AtomicInteger getDeleteCount() {
        return deleteCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getTtl() {
        return ttl;
    }

    public int getDelay() {
        return delay;
    }

    public String getCacheName() {
        return cacheName;
    }

    public Map<Class<?>, List<Model<?>>> getSaveMap() {
        return saveMap;
    }

    public long getMaxSerCostTime() {
        return serCost.values().stream().min(Comparators.comparable()).orElse(0L);
    }

    public void addSerCostTime(long costNano) {
        serCost.merge(Thread.currentThread(), costNano, Long::sum);
    }

    public void addPersistTime(long costNao) {
        persistTimeMap.merge(Thread.currentThread(), costNao, Long::sum);
    }

    public long getMaxPersistCostTime() {
        return persistTimeMap.values().stream().min(Comparators.comparable()).orElse(0L);
    }
}
