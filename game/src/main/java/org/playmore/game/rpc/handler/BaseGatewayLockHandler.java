package org.playmore.game.rpc.handler;

import com.google.protobuf.GeneratedMessage;

/**
 * @ClassName BaseGatewayLockHandler
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:45
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:45
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public abstract class BaseGatewayLockHandler<Result extends GeneratedMessage> extends BaseGatewayHandler<Result>
        implements LockHandler<Result> {

    @Override
    protected Result response() throws Exception {
        return logicInLock();
    }
}