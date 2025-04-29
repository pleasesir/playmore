package org.playmore.api.handler.abs;


import com.google.protobuf.GeneratedMessage;
import org.playmore.api.constant.GameError;
import org.playmore.api.handler.BaseRpcHandler;
import org.playmore.common.msg.BaseRpcMsg;
import org.playmore.common.util.LogUtil;

import java.util.Objects;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 23:06
 * @description TODO
 */
public abstract class BaseReturnHandler<Result extends GeneratedMessage, M extends BaseRpcMsg>
        extends BaseRpcHandler<M> {
    /**
     * 回包PB信息
     */
    protected Result rsPb;

    @Override
    public void action() throws Exception {
        rsPb = response();
    }

    @Override
    protected void onCompletion() {
        buildRsMsg();
        if (Objects.nonNull(rsMsg)) {
            super.onCompletion();
            sendMsg();
        }
    }

    /**
     * 构建响应basePb
     */
    protected void buildRsMsg() {
        if (rsMsg == null && Objects.nonNull(rsPb)) {
            if (ext() == null) {
                LogUtil.error("ext() is null, handler: ", this.getClass());
                rsMsg = CommonPbHelper.createRsBase(rsCmd, GameError.SERVER_EXCEPTION.getCode());
            } else {
                rsMsg = CommonPbHelper.createRsBase(rsCmd, ext(), rsPb).build();
            }
        }
    }

    /**
     * 创建回包msg
     *
     * @param base
     * @return
     */
    public M createRsMsg(BasePb.Base base) {
        return MsgUtil.createRsMsg(packet, base);
    }

    /**
     * 返回消息
     */
    public abstract void sendMsg();

    /**
     * 响应结果
     *
     * @return 响应结果pb
     * @throws Exception 抛错异常
     */
    protected abstract Result response() throws Exception;

    /**
     * pb扩展点
     *
     * @return pb扩展引用
     */
    protected abstract GeneratedMessage.GeneratedExtension<BasePb.Base, Result> ext();
}
