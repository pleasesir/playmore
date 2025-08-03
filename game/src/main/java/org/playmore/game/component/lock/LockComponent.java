package org.playmore.game.component.lock;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.playmore.common.util.NumberUtil;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName LockComponent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:47
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:47
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Component
public class LockComponent {

    /**
     * lock缓存cache
     */
    private final LoadingCache<String, ReentrantLock> cache = Caffeine.newBuilder()
            // 1分钟后失效
            .expireAfterAccess(1, TimeUnit.MINUTES)
            // 缓存最大数量
            .maximumSize(NumberUtil.TEN_THOUSAND)
            .scheduler(Scheduler.systemScheduler()).build(key -> new ReentrantLock());


    /**
     * 获取key对应的lock
     *
     * @param key 唯一键值
     * @return 对应lock
     */
    public ReentrantLock getLock(String key) {
        return cache.get(key);
    }
}
