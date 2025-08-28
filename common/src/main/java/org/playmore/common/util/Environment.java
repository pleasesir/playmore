package org.playmore.common.util;

/**
 * @ClassName Environment
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/29 00:22
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/29 00:22
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public enum Environment {
    /**
     * 开发环境
     */
    DEV("dev"),

    /**
     * 测试环境
     */
    TEST("test"),

    /**
     * 预发布环境
     */
    PRE_RELEASE("pre-release"),

    /**
     * 生产环境
     */
    RELEASE("release"),
    ;

    private final String env;

    Environment(String env) {
        this.env = env;
    }

    public static boolean isRelease(String envExt) {
        return RELEASE.env.equalsIgnoreCase(envExt);
    }

    public static boolean isDev(String envExt) {
        return (DEV.env.equalsIgnoreCase(envExt) ||
                TEST.env.equalsIgnoreCase(envExt) ||
                PRE_RELEASE.env.equalsIgnoreCase(envExt));
    }
}
