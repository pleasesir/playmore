package org.playmore.game.rpc.handler;

import com.google.protobuf.GeneratedMessage;

/**
 * @ClassName PlayerEventHandler
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/21 00:16
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/21 00:16
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface PlayerEventHandler<Result extends GeneratedMessage> {


    Result playerEvent() throws Exception;

}
