package org.playmore.chat.cache.component;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-09-13 14:47
 */
public abstract class StoredCacheVO implements ICacheVO {
    /**
     * 过期时间, -1 一直在内存中
     */
    protected long expiryTime;
    /**
     * 持久化的次数
     */
    protected int storedCount;
    /**
     * 本对象的初始值
     */
    protected long rawValue;

    @Override
    public long getExpiryTime() {
        return expiryTime;
    }

    @Override
    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }
}
