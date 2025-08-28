package org.playmore.game.rpc.handler;

import com.google.protobuf.GeneratedMessage;
import io.micrometer.core.instrument.MeterRegistry;
import org.playmore.api.config.AppContext;
import org.playmore.api.handler.abs.BaseReturnHandler;
import org.playmore.api.util.HandlerUtil;
import org.playmore.common.msg.impl.GatewayMsg;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.NumberUtil;
import org.playmore.game.component.net.NetComponent;
import org.playmore.pb.BasePb;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName BaseGatewayHandler
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/6/17 22:55
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/6/17 22:55
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public abstract class BaseGatewayHandler<Result extends GeneratedMessage> extends BaseReturnHandler<Result, GatewayMsg> {
    private final MeterRegistry meterRegistry;


    public BaseGatewayHandler() {
        this.meterRegistry = AppContext.getBean(MeterRegistry.class);
    }

    @Override
    protected void onCompletion() {
        buildRsMsg();
        if (Objects.nonNull(rsMsg)) {
            long roleId = playerActor == null ? 0 : playerActor.getRoleId();
            long costMills;
            if ((costMills = System.currentTimeMillis() - startTime) >= NumberUtil.HALF_OF_HUNDRED) {
                LogUtil.warn(this.getClass(), "roleId:", roleId, ", costMills:", costMills, "ms");
            }
            sendMsg();
        }
        monitor();
    }

    void monitor() {
        meterRegistry.timer("game_request_timer", "cmdId", String.valueOf(rqCmd).intern())
                .record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendMsg() {
        NetComponent.getInstance().publish(packet.getSeqId().hashCode(), packet.getFromServerId(),
                createRsMsg(rsMsg), rsMsg);
    }

    @Override
    public GatewayMsg createRsMsg(BasePb.Base base) {
        if (base == null) {
            return null;
        }
        long roleId;
        if (playerActor == null) {
            roleId = 0;
        } else {
            roleId = playerActor.getRoleId();
        }

        GatewayMsg rsMsg = GatewayMsg.createMsg(packet.getSeqId(), base.toByteArray());
        rsMsg.setRoleId(roleId);
        rsMsg.setCmdId(rsCmd);
        return rsMsg;
    }

    @Override
    public void handleInvokeThrowable(Throwable t) {
        rsMsg = HandlerUtil.handleInThrowable(rsCmd, t);
    }
}
