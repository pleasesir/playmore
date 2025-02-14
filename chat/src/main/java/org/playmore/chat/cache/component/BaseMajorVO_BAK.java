package org.playmore.chat.cache.component;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * @author Administrator
 */
public abstract class BaseMajorVO_BAK<K, V extends Model<?>> implements ICacheVO {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 对象集合
     */
    protected final Map<K, SimpleCacheVO<V>> data = new ConcurrentHashMap<>();
    /**
     * 删除集合
     */
    protected Set<SimpleCacheVO<V>> delSet;
    /**
     * 对象锁
     */
    protected Lock lock = new ReentrantLock();

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
                if (delSet == null) {
                    delSet = new HashSet<>(3);
                }
                delSet.add(vo);
            } finally {
                lock.unlock();
            }
        } else {
            logger.error(String.format("%s, not found key :%s", this.getClass().getSimpleName(), k));
        }
    }

    /**
     * 检测删除的对象
     *
     * @param delMap
     */
    private boolean doDeleteCheck(Map<Class<?>, List<Serializable>> delMap) {
        lock.lock();
        try {
            if (delSet != null && !delSet.isEmpty()) {
                Class<?> cls = null;
                for (SimpleCacheVO<V> vo : delSet) {
                    try {
                        if (cls == null) {
                            cls = vo.getModel().getClass();
                        }
                        List<Serializable> delList = delMap.computeIfAbsent(cls, k -> new ArrayList<>(delSet.size()));
                        delList.add(vo.getModel().pkVal());
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
                delSet.clear();
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public int size() {
        lock.lock();
        try {
            return data.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isPerpetual() {
        return false;
    }

    public int getEntityCount() {
        return data.size();
    }

    public void clearState() {
        lock.lock();
        try {
            delSet.clear();
        } finally {
            lock.unlock();
        }
    }

}
