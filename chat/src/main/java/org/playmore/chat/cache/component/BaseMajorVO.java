package org.playmore.chat.cache.component;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * 缓存VO
 *
 * @author Administrator
 */
public abstract class BaseMajorVO<K, V extends Model<?>> extends StoredCacheVO implements ICacheVO {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 对象集合
     */
    protected final Map<K, SimpleCacheVO<V>> data;
    /**
     * 删除集合
     */
    protected Set<SimpleCacheVO<V>> delSet;
    /**
     * 对象锁
     */
    protected Lock lock = new ReentrantLock();

    protected Lock persistLock = new ReentrantLock();

    public BaseMajorVO() {
        this.data = new ConcurrentHashMap<>();
        this.delSet = ConcurrentHashMap.newKeySet();
    }

    public BaseMajorVO(Map<K, SimpleCacheVO<V>> dataMap) {
        this.data = dataMap;
        this.delSet = ConcurrentHashMap.newKeySet();
    }

    public SimpleCacheVO<V> get(K k) {
        return data.get(k);
    }

    public Map<K, SimpleCacheVO<V>> getUnmodifiableMap() {
        return Collections.unmodifiableMap(data);
    }


    public SimpleCacheVO<V> computeIfAbsent(K k, Function<? super K, SimpleCacheVO<V>> mappingFunction) {
        lock.lock();
        try {
            return data.computeIfAbsent(k, mappingFunction);
        } finally {
            lock.unlock();
        }
    }


    public void put(K k, SimpleCacheVO<V> simpleCacheVO) {
        lock.lock();
        try {
            data.put(k, simpleCacheVO);
        } finally {
            lock.unlock();
        }
    }

    public void remove(K k) {
        SimpleCacheVO<V> vo = data.remove(k);
        if (vo != null && vo.getModel() != null) {
            lock.lock();
            try {
                delSet.add(vo);
            } finally {
                lock.unlock();
            }
        } else {
            logger.error(String.format("%s, not found key :%s", this.getClass().getSimpleName(), k));
        }
    }

    @Override
    public boolean isChange() {
        if (CheckNull.nonEmpty(delSet)) {
            return true;
        }
        if (!data.isEmpty()) {
            for (Entry<K, SimpleCacheVO<V>> entry : data.entrySet()) {
                if (entry.getValue().isChange()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void doStateCheck(Object key, PersistContext ctx) {
        //先处理删除数据
        if (CheckNull.nonEmpty(delSet)) {
            delete(key, ctx);
        }
        for (Entry<K, SimpleCacheVO<V>> entry : data.entrySet()) {
            SimpleCacheVO<V> simpleCacheVO = entry.getValue();
            simpleCacheVO.setExpiryTime(expiryTime);
            simpleCacheVO.doStateCheck(key, ctx);
            if (simpleCacheVO.getExpiryTime() > expiryTime) {
                this.expiryTime = simpleCacheVO.getExpiryTime();
            }
        }
    }

    private void delete(Object key, PersistContext ctx) {
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
                        logger.error("缓存KEY :{}, 删除 pk :{}, 失败!!!, model :{}", key, pkVal, model, e);
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
