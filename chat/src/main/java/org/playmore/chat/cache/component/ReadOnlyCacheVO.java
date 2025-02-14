package org.playmore.chat.cache.component;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-09-14 14:52
 */
public class ReadOnlyCacheVO<T> implements ICacheVO {
    /**
     * 过期时间, -1 一直在内存中
     */
    protected long expiryTime;
    private final T t;

    public ReadOnlyCacheVO(T t) {
        this.t = t;
    }

    @Override
    public void doStateCheck(Object key, PersistContext ctx) {
        //do nothing
    }

    @Override
    public long getExpiryTime() {
        return expiryTime;
    }

    @Override
    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    @Override
    public boolean isChange() {
        return false;
    }

    public T get() {
        return t;
    }
}
