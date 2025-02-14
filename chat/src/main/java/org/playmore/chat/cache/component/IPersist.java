package org.playmore.chat.cache.component;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

/**
 * 清除缓存策略
 *
 * @author dahu
 */
public interface IPersist<K, V> {
    /**
     * 清除指定的KEY缓存
     *
     * @param key
     * @return
     */
    V removeSpecifyCache(K key);

    /**
     * 删除指定KEY 类型的数据
     *
     * @param keyType CacheKeyRule 定义的类型
     * @return
     */
    Map<K, V> removeSpecifyTypeCache(K keyType);

    /**
     * 移除指定缓存key 前缀的数据
     *
     * @param keyType
     * @param params
     * @return
     */
    Map<K, V> removeSpecifyTypeCache(K keyType, Object... params);

    /**
     * 清除缓存
     *
     * @return
     */
    boolean clear();

    /**
     * 初始化
     */
    void init();

    /**
     * 关闭
     */
    void destroy();

    /**
     * 清除缓存
     *
     * @param close
     */
    void persist(boolean close);

    /**
     * 获取缓存服务
     *
     * @return
     */
    ICacheStoreService getCacheStoreService();

    /**
     * 获取Mapper
     *
     * @param entityClass
     * @return
     */
    BaseMapper<?> getMapperByClass(Class<?> entityClass);
}
