package org.playmore.api.handler.abs;


import org.playmore.api.handler.BaseRpcHandler;
import org.playmore.common.msg.BaseRpcMsg;
import org.playmore.common.util.ExceptionUtil;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 23:12
 * @description TODO
 */
public abstract class BaseVoidHandler<M extends BaseRpcMsg> extends BaseRpcHandler<M> {
    @Override
    protected void handleInvokeThrowable(Throwable t) {
        ExceptionUtil.analysisThrowable(getClass(), t);
    }

    @Override
    protected void onCompletion() {
        // do nothing
    }
}
