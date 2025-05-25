package org.playmore.api.handler.abs;


import com.google.protobuf.GeneratedMessage;
import org.playmore.api.config.AppContext;
import org.playmore.api.exception.GameError;
import org.playmore.api.exception.MwException;
import org.playmore.common.exception.TreasureException;
import org.playmore.common.msg.BaseRpcMsg;
import org.playmore.common.util.ExceptionUtil;
import org.playmore.common.util.LogUtil;

import java.util.concurrent.CompletableFuture;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 23:08
 * @description TODO
 */
public abstract class BaseFutureHandler<Result extends GeneratedMessage, Msg extends BaseRpcMsg>
        extends BaseReturnHandler<Result, Msg> {
    /**
     * 回包监听
     */
    protected CompletableFuture<Msg> future;

    public BaseFutureHandler() {
        super();
        future = new CompletableFuture<>();
    }

    @Override
    public void sendMsg() {
        try {
            future.complete(createRsMsg(rsMsg));
        } catch (Throwable t) {
            ExceptionUtil.analysisThrowable(getClass(), t);
        }
    }

    @Override
    public void handleInvokeThrowable(Throwable t) {
        String serviceName = AppContext.getContext().getEnvironment().getProperty("spring.application.name");
        serviceName = serviceName == null ? "<nil>" : serviceName;
        Throwable throwable;
        Throwable ex = (throwable = t.getCause()) == null ? t : throwable;
        if (t instanceof RuntimeException runtime) {
            ex = runtime.getCause() != null ? runtime.getCause() : runtime;
        }
        if (ex instanceof TreasureException mwe) {
            LogUtil.warn("class: ", getClass(), ", ex: ", t);
            future.completeExceptionally(new MwException(mwe.getCode(), serviceName, t));
        } else {
            LogUtil.error("", t);
            future.completeExceptionally(new MwException(GameError.SERVER_EXCEPTION, serviceName, t));
        }
    }
}
