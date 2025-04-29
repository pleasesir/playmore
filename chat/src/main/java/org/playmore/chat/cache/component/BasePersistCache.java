package org.playmore.chat.cache.component;

import cn.hutool.core.thread.NamedThreadFactory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @Description
 * @Author zhangdh
 * @Date 2021-09-09 15:56
 */
public abstract class BasePersistCache<K, T extends ICacheVO> implements IPersist<K, T> {
    private static final String CACHE_SAVE_FILE_DIR = "cache";
    private static final File CACHE_DIR = new FileSystemResource(CACHE_SAVE_FILE_DIR).getFile();
    protected String cacheName = getClass().getSimpleName();
    protected ConcurrentHashMap<K, T>[] caches;
    protected int moduleSize = 5;
    /**
     * 缓存过期时间(默认15分钟)
     */
    protected int ttl = 900000;
    /**
     * 每次获取后比较延长时间5分钟
     */
    protected int delay = 300000;

    //入库线程池
    protected ExecutorService cacheStoreThreadPool;

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        caches = new ConcurrentHashMap[moduleSize];
        for (int i = 0; i < moduleSize; i++) {
            caches[i] = new ConcurrentHashMap<>();
        }
        cacheStoreThreadPool = new ThreadPoolExecutor(moduleSize, moduleSize, 15,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000),
                new NamedThreadFactory("cache-store-thread", false));
        if (!CACHE_DIR.exists()) {
            if (CACHE_DIR.mkdir()) {
            }
        }
    }

    @Override
    public T removeSpecifyCache(K key) {
        int i = getIndex(key);
        return caches[i].remove(key);
    }

    @Override
    public Map<K, T> removeSpecifyTypeCache(K keyType) {
        return null;
    }

    @Override
    public boolean clear() {
        for (ConcurrentHashMap<K, T> cacheMap : caches) {
            cacheMap.clear();
        }
        return true;
    }

    @Override
    public void destroy() {
        cacheStoreThreadPool.shutdown();
    }

    protected int getIndex(K key) {
        int hashCode = key.hashCode();
        return Math.abs(hashCode) % moduleSize;
    }


    public T getProperty(K key, boolean isLocal) {
        int i = getIndex(key);
        T t = caches[i].get(key);
        if (t == null && !isLocal) {
            try {
                synchronized (caches[i]) {
                    t = caches[i].get(key);
                    if (t != null) {
                        return t;
                    }
                    long startNano = System.nanoTime();
                    T find = findProperty(key);
                    long costMill = (System.nanoTime() - startNano) / 1000000;
                    if (costMill > 100) {
                    }
                    if (find == null) {
                    } else {
                        t = caches[i].computeIfAbsent(key, k -> find);
                        t.setExpiryTime(System.currentTimeMillis() + getTtl(t));
                    }
                }
            } catch (Exception e) {
            }
        } else if (t != null) {
            long nowMill = System.currentTimeMillis();
            long expiryTime = t.getExpiryTime();
            if (expiryTime - nowMill < getDelay()) {
                t.setExpiryTime(nowMill + getTtl(t));
            }
        }
        return t;
    }

    protected long getTtl(T t) {
        return ttl;
    }

    protected long getDelay() {
        return delay;
    }

    protected abstract T findProperty(K key);

    public Map<K, T> getPropertyList(boolean isLocal, Collection<K> keyList) {
        Map<K, T> map = new HashMap<>();
        List<K> keyList1 = new ArrayList<>();
        long now = System.currentTimeMillis();
        long newDelay = now + getTtl(null);
        for (K key : keyList) {
            int i = getIndex(key);
            T t = caches[i].get(key);
            if (t != null) {
                if ((t.getExpiryTime() - now) < getDelay()) {
                    t.setExpiryTime(newDelay);
                }
                map.put(key, t);
            } else if (!isLocal) {
                keyList1.add(key);
            }
        }
        if (!isLocal && !keyList1.isEmpty()) {
            try {
                long startNano = System.nanoTime();
                Map<K, T> findMap = findPropertyMap(keyList1);
                if (findMap == null) {
                } else {
                    for (Map.Entry<K, T> entry : findMap.entrySet()) {
                        K key = entry.getKey();
                        T find = entry.getValue();
                        int i = getIndex(key);
                        synchronized (caches[i]) {
                            T t = caches[i].computeIfAbsent(key, k -> find);
                            t.setExpiryTime(System.currentTimeMillis() + getTtl(t));
                            map.put(key, t);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        return map;
    }

    /**
     * 批量获取缓存
     *
     * @param keyList
     * @return
     * @throws Exception
     */
    protected abstract Map<K, T> findPropertyMap(List<K> keyList) throws Exception;


    /**
     * 默认为定时清除缓存, 或停止服务钩子调用
     *
     * @param close
     */
    @Override
    public synchronized void persist(boolean close) {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(moduleSize);
            PersistContext ctx = new PersistContext(cacheName, (int) getTtl(null), (int) getDelay());
            for (ConcurrentHashMap<K, T> cacheMap : caches) {
                cacheStoreThreadPool.execute(() -> {
                    try {
                        for (Map.Entry<K, T> entry : cacheMap.entrySet()) {
                            T v = entry.getValue();
                            K k = entry.getKey();
                            if (Objects.isNull(v)) {
                                cacheMap.remove(k);
                                continue;
                            }
                            v.doStateCheck(k, ctx);
                            if (ctx.getStartTime() >= v.getExpiryTime()) {
                                cacheMap.remove(entry.getKey());
                            }
                        }
                    } catch (Exception e) {
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await();
            int failCount = getSaveFailModelCount(ctx);
            if (failCount > 0 && close) {
                saveCacheObjects2File(ctx);
            }
        } catch (Exception e) {
        }
    }

    private void saveCacheObjects2File(PersistContext ctx) {
        try {
            Map<Class<?>, List<Model<?>>> saveMap = ctx.getSaveMap();
            if (CheckNull.nonEmpty(saveMap)) {
                File timeDir = new File(CACHE_DIR.getPath() + "/" + ctx.getStartTime());
                if (!timeDir.exists() && timeDir.mkdir()) {
                }
                for (Map.Entry<Class<?>, List<Model<?>>> entry : saveMap.entrySet()) {
                }
            }
        } catch (Exception e) {
        }
    }

    private int getSaveFailModelCount(PersistContext ctx) {
        int totalCount = 0;
        for (Map.Entry<Class<?>, List<Model<?>>> entry : ctx.getSaveMap().entrySet()) {
            totalCount += entry.getValue().size();
        }
        return totalCount;
    }


    @Override
    public ICacheStoreService getCacheStoreService() {
        return null;
    }

    @Override
    public BaseMapper<?> getMapperByClass(Class<?> entityClass) {
        return null;
    }
}
