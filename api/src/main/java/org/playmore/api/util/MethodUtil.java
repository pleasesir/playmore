package org.playmore.api.util;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * @ClassName MethodUtil
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:15
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:15
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class MethodUtil {
    /**
     * 获取 (类名-方法名)
     *
     * @param access   方法访问器
     * @param instance 实例
     * @param index    方法索引
     * @return 简单类类名-方法名
     */
    public static String consumerName(MethodAccess access, Object instance, int index) {
        String clazzName = instance.getClass().getSimpleName();
        String methodName = access.getMethodNames()[index];
        return clazzName + "-" + methodName;
    }

    public static String consumerName(Class<?> clazz, String methodName) {
        return clazz.getSimpleName() + "-" + methodName;
    }
}
