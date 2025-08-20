package org.playmore.game.rpc.handler;

import com.google.protobuf.GeneratedMessage;
import org.playmore.api.domain.PlayerEntity;

import static org.playmore.api.util.VertxUtil.actor;

/**
 * @ClassName BasePlayerHandler
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/6/17 22:55
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/6/17 22:55
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public abstract class BasePlayerHandler<Result extends GeneratedMessage> extends BaseGatewayHandler<Result>
        implements PlayerEventHandler<Result> {

    protected PlayerEntity actor;

    @Override
    protected Result response() throws Exception {
        actor = actor();
        return playerEvent();
    }
}
