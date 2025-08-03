package org.playmore.api.util;

import org.apache.dubbo.remoting.ExecutionException;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.TimeoutException;
import org.apache.dubbo.rpc.RpcException;
import org.playmore.api.exception.GameError;
import org.playmore.common.exception.TreasureException;
import org.playmore.common.util.LogUtil;
import org.playmore.pb.BasePb;

/**
 * @ClassName HandlerUtil
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:30
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:30
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class HandlerUtil {

    /**
     * 处理异常情况并返回相应的BasePb.Base对象
     * 此方法用于处理在执行操作时抛出的异常，根据异常的类型和原因返回不同的错误代码和信息
     *
     * @param rsCmd 操作命令代码，用于标识执行的操作
     * @param t     异常对象，包含操作失败的原因
     * @return 返回一个BasePb.Base对象，其中包含错误代码和相关信息
     */
    public static BasePb.Base handleInThrowable(int rsCmd, Throwable t) {
        // 获取异常的根本原因，如果没有则使用当前异常
        Throwable throwable;
        Throwable ex = (throwable = t.getCause()) == null ? t : throwable;

        // 如果异常是TreasureException类型，则转换并返回相应的错误信息
        if (ex instanceof TreasureException mwe) {
            // 记录警告日志
            LogUtil.warn(ex.toString(), ex);
            // 创建并返回错误基础信息
            return CommonPbHelper.createRsBase(rsCmd, mwe.getCode());
        } else if (ex instanceof RemotingException) {
            // 记录错误日志
            LogUtil.error(ex.getMessage(), ex);
            GameError error;
            // 根据异常类型确定具体的错误
            if (ex instanceof ExecutionException) {
                error = GameError.INVOKER_FAIL;
            } else if (ex instanceof TimeoutException) {
                error = GameError.INVOKER_TIMEOUT;
            } else {
                error = GameError.SERVER_CONNECT_EXCEPTION;
            }
            // 创建并返回错误基础信息
            return CommonPbHelper.createRsBase(rsCmd, error.getCode());
        } else if (ex instanceof RpcException) {
            // 记录错误日志
            LogUtil.error(ex.getMessage(), ex);
            RpcException e = (RpcException) ex;
            GameError error = GameError.SERVER_CONNECT_FAIL;
            // 判断RPC异常的具体类型，如果是特定的FORBIDDEN异常，则返回不同的错误代码
            if (e.getCode() == RpcException.FORBIDDEN_EXCEPTION) {
                error = GameError.SERVER_NOT_FOUND;
            }
            // 创建并返回错误基础信息
            return CommonPbHelper.createRsBase(rsCmd, error.getCode());
        } else {
            // 对于未处理的其他类型异常，记录错误日志并返回未知错误代码
            LogUtil.error(" Not Hand  Exception -->" + ex.getMessage(), ex);
            // 创建并返回未知错误基础信息
            return CommonPbHelper.createRsBase(rsCmd, GameError.UNKNOWN_ERROR.getCode());
        }
    }
}
