package org.playmore.api.rpc.game;

import org.playmore.common.msg.BaseRpcMsg;

/**
 * @ClassName GameServerRpcService
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/29 00:26
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/29 00:26
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface GameServerRpcService {

    /**
     * 推送
     *
     * @param msg 消息
     */
    void cast(BaseRpcMsg msg);

    /**
     * 调用
     *
     * @param msg 消息
     * @return 响应
     */
    BaseRpcMsg call(BaseRpcMsg msg);

}
