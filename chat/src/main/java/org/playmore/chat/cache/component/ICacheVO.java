package org.playmore.chat.cache.component;

public interface ICacheVO {

    /**
     * 对象状态检测
     *
     * @param key 缓存key
     * @return
     */
    void doStateCheck(Object key, PersistContext ctx);

    long getExpiryTime();

    void setExpiryTime(long expiryTime);

    boolean isChange();

}