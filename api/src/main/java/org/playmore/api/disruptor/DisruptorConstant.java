package org.playmore.api.disruptor;

/**
 * @ClassName DisruptorConstant
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/25 22:33
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/25 22:33
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface DisruptorConstant {

    int DEFAULT_TIME_OUT_MS = 5000;
    int SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS = 50;
    int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 200;
}
