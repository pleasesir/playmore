package org.playmore.game.rpc.handler;

import com.google.protobuf.GeneratedMessage;
import org.playmore.api.config.AppContext;
import org.playmore.game.component.lock.LockComponent;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName LockHandler
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:46
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:46
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface LockHandler<Result extends GeneratedMessage> {
    /**
     * 在锁中执行, 保证当前协议请求有序性
     *
     * @return 返回结果pb
     * @throws Exception 抛出异常
     */
    Result responseInLock() throws Exception;

    /**
     * handler锁唯一id
     *
     * @return 唯一id
     */
    String lockKey();

    /**
     * 锁中执行逻辑
     *
     * @return 锁
     */
    default Result logicInLock() throws Exception {
        ReentrantLock lock = AppContext.getBean(LockComponent.class).getLock(lockKey());
        lock.lock();
        try {
            return responseInLock();
        } finally {
            lock.unlock();
        }
    }
}
