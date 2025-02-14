package org.playmore.chat.cache.component;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象缓存类
 * K:缓存KEY
 * V:缓存对象
 *
 * @param <T>
 * @author dahu
 */
public abstract class AbstractPersistCache<K, T> implements IPersist<K, T> {
    protected Logger log = LoggerFactory.getLogger(getClass());
    protected ConcurrentHashMap<K, T>[] caches;
    protected ConcurrentHashMap<K, Long>[] expiryCaches;
    protected int moduleSize = 5;
    // 缓存过期时间(默认15分钟)
    protected int ttl = 900000;
    // 每次获取后比较延长时间5分钟
    protected int delay = 300000;

    public AbstractPersistCache() {

    }

    public boolean clearCacheKey(K key) {
        int i = getIndex(key);
        expiryCaches[i].put(key, 0L);
        persist(false);
        return true;
    }

    public abstract boolean clearCacheByRuleKey(K keyRule);

    protected void addDelItem(Map<Class<?>, List<Serializable>> delMap, Class<?> cl,
                              Serializable delId) {
        List<Serializable> delList = delMap.computeIfAbsent(cl, k -> new ArrayList<>());
        delList.add(delId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        caches = new ConcurrentHashMap[moduleSize];
        expiryCaches = new ConcurrentHashMap[moduleSize];
        for (int i = 0; i < moduleSize; i++) {
            caches[i] = new ConcurrentHashMap<>();
            expiryCaches[i] = new ConcurrentHashMap<>();
        }
    }

    public T getProperty(int i, K key) {
        T t = caches[i].get(key);
        if (t != null) {
            if (log.isInfoEnabled()) {
                log.info("获得信息:" + key);
            }
            long now = System.currentTimeMillis();
            if ((expiryCaches[i].get(key) - now) < delay) {
                long expiredTime = now + ttl;
                expiryCaches[i].put(key, expiredTime);
                if (log.isInfoEnabled()) {
                }
            }
        }
        return t;
    }

    @SafeVarargs
    public final Map<K, T> getProperties(K... keys) {
        Map<K, T> map = new HashMap<>();
        long nowMill = System.currentTimeMillis();
        long newDelay = nowMill + ttl;
        for (K key : keys) {
            int i = getIndex(key);
            T t = caches[i].get(key);
            if (t != null) {
                if ((expiryCaches[i].get(key) - nowMill) < delay) {
                    expiryCaches[i].put(key, newDelay);
                    if (log.isInfoEnabled()) {
                    }
                }
                map.put(key, t);
            }
        }
        return map;
    }

    public T getProperty(K key, boolean isLocal) {
        int i = getIndex(key);
        T t = caches[i].get(key);
        if (t == null && !isLocal) {
            try {
                long startNano = System.nanoTime();
                T find = findProperty(key);
                long costMill = (System.nanoTime() - startNano) / 1000000;
                if (costMill > 30) {
                    log.warn("key {} DB 操作花费时间 {} 毫秒", key, costMill);
                }
                if (find == null) {
                    log.error(getClass().getSimpleName() + " can not found key: " + key);
                } else {
                    synchronized (caches[i]) {
                        t = caches[i].get(key);
                        if (t == null) {
                            t = find;
                            put(i, key, t);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        } else if (t != null) {
            long nowMill = System.currentTimeMillis();
            if ((expiryCaches[i].get(key) - nowMill) < delay) {
                long expiredTime = nowMill + ttl;
                expiryCaches[i].put(key, expiredTime);
                if (log.isInfoEnabled()) {
                }
            }
        }
        return t;
    }

    @SafeVarargs
    public final Map<K, T> getPropertys(boolean isLocal, K... keys) {
        Map<K, T> map = new HashMap<>();
        List<K> keyList = new ArrayList<>();
        long nowMill = System.currentTimeMillis();
        long newDelay = nowMill + ttl;
        for (K key : keys) {
            int i = getIndex(key);
            T t = caches[i].get(key);
            if (t != null) {
                if ((expiryCaches[i].get(key) - nowMill) < delay) {
                    expiryCaches[i].put(key, newDelay);
                    if (log.isInfoEnabled()) {
                    }
                }
                map.put(key, t);
            } else if (!isLocal) {
                keyList.add(key);
            }
        }
        if (!isLocal && !keyList.isEmpty()) {
            try {
                Map<K, T> findMap = findPropertys(keyList);
                if (findMap == null) {
                    log.error(getClass().getSimpleName() + " can not found keys:" + keyList);
                } else {
                    for (Entry<K, T> entry : findMap.entrySet()) {
                        K key = entry.getKey();
                        T find = entry.getValue();
                        int i = getIndex(key);
                        synchronized (caches[i]) {
                            T t = caches[i].get(key);
                            if (t != null) {
                                map.put(key, t);
                            } else {
                                put(i, key, find);
                                map.put(key, find);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return map.isEmpty() ? null : map;
    }

    public Map<K, T> getPropertyList(boolean isLocal, Collection<K> keyList) {
        Map<K, T> map = new HashMap<>();
        List<K> keyList1 = new ArrayList<>();
        long now = System.currentTimeMillis();
        long newDelay = now + ttl;
        for (K key : keyList) {
            int i = getIndex(key);
            T t = caches[i].get(key);
            if (t != null) {
                if ((expiryCaches[i].get(key) - now) < delay) {
                    expiryCaches[i].put(key, newDelay);
                    if (log.isInfoEnabled()) {
                    }
                }
                map.put(key, t);
            } else if (!isLocal) {
                keyList1.add(key);
            }
        }
        if (!isLocal && !keyList1.isEmpty()) {
            try {
                long startNano = System.nanoTime();
                Map<K, T> findMap = findPropertys(keyList1);
                long costMill = (System.nanoTime() - startNano) / 1000000;
                if (costMill > 30) {
                    log.warn("key {} DB 操作花费时间 {} 毫秒, keySize {}",
                            keyList1.get(0).toString().split("_")[0], costMill, keyList1.size());
                }
                if (findMap == null) {
                    log.error(getClass().getSimpleName() + " can not found keys:" + keyList1);
                } else {
                    for (Entry<K, T> entry : findMap.entrySet()) {
                        K key = entry.getKey();
                        T find = entry.getValue();
                        int i = getIndex(key);
                        synchronized (caches[i]) {
                            T t = caches[i].get(key);
                            if (t != null) {
                                map.put(key, t);
                            } else {
                                put(i, key, find);
                                map.put(key, find);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return map;
    }

    @Override
    public boolean clear() {
        if (caches != null) {
            for (int i = 0; i < caches.length; i++) {
                caches[i].clear();
                expiryCaches[i].clear();
            }
        }
        log.info(String.format("%s clear cache succ ... Thread Name : [ %s ]", getClass().getSimpleName(), Thread.currentThread().getName()));
        return true;
    }

    public T put(K key, T value, long ttl) {
        int i = getIndex(key);
        return put(i, key, value, ttl);
    }

    public T put(int i, K key, T value, long ttl) {
        T result = caches[i].put(key, value);
        expiryCaches[i].put(key, ttl);
        if (log.isInfoEnabled()) {
            log.debug("缓存加入时：KEY IS " + key + ",VALUE:" + value);
        }
        return result;
    }

    public T put(K key, T value, Date expiry) {
        return put(key, value, expiry.getTime());
    }

    public T put(int i, K key, T value) {
        return put(key, value, System.currentTimeMillis() + ttl);
    }

    public T remove(K key) {
        int i = getIndex(key);
        T result = caches[i].remove(key);
        expiryCaches[i].remove(key);
        return result;
    }

    public Collection<Object> values() {
        Collection<Object> values = new ArrayList<>();
        for (ConcurrentHashMap<K, T> cache : caches) {
            values.addAll(cache.values());
        }
        return values;
    }

    protected int getIndex(K key) {
        int hashCode = key.hashCode();
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        return hashCode % moduleSize;
    }

    protected abstract T findProperty(K key) throws Exception;

    protected abstract Map<K, T> findPropertys(List<K> keyList)
            throws Exception;

    protected abstract List<Model<?>> getStoreUpdateList();

    protected abstract List<Model<?>> getStoreMergeList();

    protected abstract List<Model<?>> getStoreAddList();

    protected abstract Map<Class<?>, List<Serializable>> getStoreDelMap();

    protected abstract boolean checkCacheItem(K key, T value, List<Model<?>> updateList,
                                              List<Model<?>> addList, List<Model<?>> mergeList, Map<Class<?>, List<Serializable>> delMap);

    // 默认为定时清除缓存
    public synchronized void persist() {
        try {
            long nowMill = System.currentTimeMillis();
            List<Model<?>> updateList = getStoreUpdateList();
            List<Model<?>> addList = getStoreAddList();
            List<Model<?>> mergeList = getStoreMergeList();
            Map<Class<?>, List<Serializable>> delMap = getStoreDelMap();
            for (int i = 0; i < caches.length; i++) {
                if (!caches[i].isEmpty()) {
                    for (Entry<K, T> entry : caches[i].entrySet()) {
                        K key = entry.getKey();
                        T value = entry.getValue();
                        boolean isChange = false;
                        if (!(value instanceof ICacheVO)) {
                            isChange = checkCacheItem(key, value, updateList, addList, mergeList, delMap);
                        }
                        if (!isChange && nowMill - expiryCaches[i].get(key) > 0) {
                            caches[i].remove(key);
                            expiryCaches[i].remove(key);
                            if (log.isInfoEnabled()) {
                                log.info("清理过期的缓存KEY :" + key);
                            }
                        }
                    }
                } else {
                    if (!expiryCaches[i].isEmpty()) {
                        expiryCaches[i].clear();
                    }
                }
            }
            int delCount = 0;
            if (delMap != null && !delMap.isEmpty()) {
                for (Class<?> c : delMap.keySet()) {
                    List<Serializable> delList = delMap.get(c);
                    getCacheStoreService().deleteBatch(c, delList);
                    delCount += delList.size();
                }
                delMap.clear();
            }
            int updateCount = 0;
            if (updateList != null && !updateList.isEmpty()) {
                getCacheStoreService().updateEntityList(updateList);
                updateCount = updateList.size();
                updateList.clear();
            }
            int insertCount = 0;
            if (addList != null && !addList.isEmpty()) {
                getCacheStoreService().insertEntityList(addList);
                insertCount = addList.size();
                addList.clear();

            }

            int mergeCount = 0;
            if (mergeList != null && !mergeList.isEmpty()) {
                getCacheStoreService().mergeEntityList(mergeList);
                mergeCount = mergeList.size();
                mergeList.clear();
            }
            long costMill = System.currentTimeMillis() - nowMill;
            log.info(String.format("%s persist cache succ ... cost %d ms, del %d update %d, insert %d merge %d",
                    getClass().getSimpleName(), costMill, delCount, updateCount, insertCount, mergeCount));
        } catch (Exception e) {
            log.error(String.format("%s persist cache fail !!!", getClass().getSimpleName()), e);
        }
    }

    public T removeCache(K key) {
        int i = getIndex(key);
        T value = caches[i].remove(key);
        expiryCaches[i].remove(key);
        if (value != null) {
            List<Model<?>> updateList = getStoreUpdateList();
            List<Model<?>> addList = getStoreAddList();
            List<Model<?>> mergeList = getStoreMergeList();
            Map<Class<?>, List<Serializable>> delMap = getStoreDelMap();
            checkCacheItem(key, value, updateList, addList, mergeList, delMap);
            if (delMap != null && !delMap.isEmpty()) {
                for (Class<?> c : delMap.keySet()) {
                    getCacheStoreService().deleteBatch(c, delMap.get(c));
                }
                delMap.clear();
            }
            if (updateList != null && !updateList.isEmpty()) {
                getCacheStoreService().updateEntityList(updateList);
            }
            if (addList != null && !addList.isEmpty()) {
                getCacheStoreService().insertEntityList(addList);
            }
            if (mergeList != null && !mergeList.isEmpty()) {
                getCacheStoreService().mergeEntityList(mergeList);
            }
        }
        try {
            log.error(String.format("%s, removeCache key :%s, value :%s fail!!!", getClass().getSimpleName(), key, JSONObject.toJSONString(value)));
        } catch (Exception e) {
            log.error("", e);
        }
        return value;
    }

    @Override
    public void destroy() {

    }

    public void setModuleSize(int moduleSize) {
        this.moduleSize = moduleSize;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

}
