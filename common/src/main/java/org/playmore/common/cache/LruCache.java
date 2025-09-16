package org.playmore.common.cache;

import java.io.Serial;
import java.util.LinkedHashMap;

/**
 * @ClassName LruCache
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/9/10 23:14
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/9/10 23:14
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {
    @Serial
    private static final long serialVersionUID = 1674846465563293597L;
}
