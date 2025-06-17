package org.playmore.api.util;

import org.playmore.common.msg.BaseRpcMsg;

/**
 * @ClassName MsgUtil
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/6/17 22:44
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/6/17 22:44
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class MsgUtil {

    public static <T extends BaseRpcMsg> T createRsMsg(BaseRpcMsg rqMsg, BasePb.Base base) {
        if (rqMsg instanceof RpcMsg) {
            return (T) new RpcMsg(rqMsg.getRoleId(), rqMsg.getFromServerId(), base);
        }
    }
}
