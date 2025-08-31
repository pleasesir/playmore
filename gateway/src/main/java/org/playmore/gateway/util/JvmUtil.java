package org.playmore.gateway.util;

/**
 * @ClassName JvmUtil
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/31 23:06
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/31 23:06
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class JvmUtil {

    public static final int CORE_POOL_SIZE;

    static {
        CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    }
}
