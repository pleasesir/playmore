package org.playmore.chat.cache.component;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

/**
 * 实现2级主键
 *
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 23:39
 */
public class AbsMultiMajorVO<K, T, V extends Model<?>> extends StoredCacheVO implements ICacheVO {
    private static final Logger logger = LoggerFactory.getLogger(AbsMultiMajorVO.class);

    protected Map<K, Map<T, SimpleCacheVO<V>>> data = new ConcurrentHashMap<>();
    /**
     * 删除集合
     */
    protected Set<SimpleCacheVO<V>> delSet;
    protected Lock persistLock = new ReentrantLock();
    protected Lock lock = new ReentrantLock();


    public Map<K, Map<T, SimpleCacheVO<V>>> getUnmodifiableData() {
        return Collections.unmodifiableMap(data);
    }

    public Map<T, SimpleCacheVO<V>> get(K key1, boolean createIfAbsent) {
        Map<T, SimpleCacheVO<V>> minorMap = data.get(key1);
        if (Objects.isNull(minorMap) && createIfAbsent) {
            lock.lock();
            try {
                minorMap = data.computeIfAbsent(key1, key -> new ConcurrentHashMap<>());
            } finally {
                lock.unlock();
            }
        }
        return minorMap;
    }

    public SimpleCacheVO<V> get(K k, T t) {
        Map<T, SimpleCacheVO<V>> map = data.get(k);
        if (CheckNull.nonEmpty(map)) {
            return map.get(t);
        }
        return null;
    }


    public SimpleCacheVO<V> computeIfAbsent(K k, T t, BiFunction<K, T, SimpleCacheVO<V>> mappingFunction) {
        lock.lock();
        try {
            Map<T, SimpleCacheVO<V>> minorMap = data.computeIfAbsent(k, m -> new ConcurrentHashMap<>());
            SimpleCacheVO<V> v = mappingFunction.apply(k, t);
            return minorMap.computeIfAbsent(t, m -> v);
        } finally {
            lock.unlock();
        }
    }

    public void put(K k, T t, SimpleCacheVO<V> svo) {
        lock.lock();
        try {
            Map<T, SimpleCacheVO<V>> minorMap = data.computeIfAbsent(k, u -> new ConcurrentHashMap<>());
            minorMap.put(t, svo);
        } finally {
            lock.unlock();
        }
    }


    public void remove(K k, T t) {
        Map<T, SimpleCacheVO<V>> minorMap = data.get(k);
        if (CheckNull.nonEmpty(minorMap)) {
            SimpleCacheVO<V> vo = minorMap.remove(t);
            V model = Objects.nonNull(vo) ? vo.getModel() : null;
            if (Objects.isNull(model)) {
                return;
            }
            lock.lock();
            try {
                delSet = new HashSet<>();
                delSet.add(vo);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void doStateCheck(Object cacheKey, PersistContext ctx) {
        //先处理删除数据
        if (CheckNull.nonEmpty(delSet)) {
            delete(cacheKey, ctx);
        }
        for (Map.Entry<K, Map<T, SimpleCacheVO<V>>> majorEntry : data.entrySet()) {
            for (Map.Entry<T, SimpleCacheVO<V>> minorEntry : majorEntry.getValue().entrySet()) {
                SimpleCacheVO<V> simpleCacheVO = minorEntry.getValue();
                simpleCacheVO.setExpiryTime(expiryTime);
                simpleCacheVO.doStateCheck(cacheKey, ctx);
                if (simpleCacheVO.getExpiryTime() > expiryTime) {
                    this.expiryTime = simpleCacheVO.getExpiryTime();
                }
            }
        }
    }

    @Override
    public boolean isChange() {
        if (delSet != null && delSet.size() > 0) {
            return true;
        }
        for (Map.Entry<K, Map<T, SimpleCacheVO<V>>> entry : data.entrySet()) {
            for (Map.Entry<T, SimpleCacheVO<V>> voEntry : entry.getValue().entrySet()) {
                if (voEntry.getValue().isChange()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void delete(Object cacheKey, PersistContext ctx) {
        persistLock.lock();
        try {
            if (CheckNull.isEmpty(delSet)) {
                return;
            }
            Set<SimpleCacheVO<V>> fixRmSet = new HashSet<>();
            boolean delException = false;
            for (SimpleCacheVO<V> deleteModel : delSet) {
                V model = deleteModel.getModel();
                Serializable pkVal = model != null ? model.pkVal() : null;
                if (pkVal == null) {
                    fixRmSet.add(deleteModel);
                } else {
                    try {
                        model.deleteById();
                        fixRmSet.add(deleteModel);
                        ctx.getDeleteCount().incrementAndGet();
                    } catch (Exception e) {
                        delException = true;
                        logger.error("缓存KEY :{}, 删除 pk :{}, 失败!!!, model :{}", cacheKey, pkVal, model, e);
                    }
                }
            }
            if (!fixRmSet.isEmpty()) {
                for (SimpleCacheVO<V> deleteModel : fixRmSet) {
                    delSet.remove(deleteModel);
                }
            }
            if (delException) {
                expiryTime += ctx.getTtl();
            }
        } finally {
            persistLock.unlock();
        }
    }
}
