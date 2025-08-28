package org.playmore.gateway.net.codec.factory;

import lombok.Getter;
import org.playmore.api.config.AppContext;
import org.playmore.gateway.component.GateExecutorComponent;
import org.playmore.gateway.component.GateServerComponent;
import org.playmore.gateway.net.codec.decode.ConfirmDecoder;
import org.playmore.gateway.net.codec.decode.WsMsgDecoder;
import org.playmore.gateway.net.codec.encode.BatchWsMsEncoder;
import org.playmore.gateway.net.codec.encode.WsMsgEncoder;
import org.playmore.gateway.net.pipeline.GatewayMessageDispatcher;

/**
 * @ClassName MessageCodecFactory
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 00:14
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 00:14
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class MessageCodecFactory {
    private static final WsMsgEncoder MSG_ENCODER = new WsMsgEncoder();
    private static final WsMsgDecoder MSG_DECODER = new WsMsgDecoder();
    private static final ConfirmDecoder CONFIRM_DECODER = new ConfirmDecoder();
    private static final BatchWsMsEncoder BATCH_WS_MS_ENCODER = new BatchWsMsEncoder();
    @Getter
    private static GatewayMessageDispatcher gatewayMessageDispatcher;

    public static void initData() {
        CONFIRM_DECODER.setExecutor(AppContext.getBean(GateExecutorComponent.class));
        gatewayMessageDispatcher = new GatewayMessageDispatcher(AppContext.getBean(GateServerComponent.class)
                .getGatewayNettyServer());
    }

    public static WsMsgEncoder getWsEncoder() {
        return MSG_ENCODER;
    }

    public static WsMsgDecoder getWsDecoder() {
        return MSG_DECODER;
    }

    public static ConfirmDecoder getConfirmDecoder() {
        return CONFIRM_DECODER;
    }

    public static BatchWsMsEncoder getBatchWsMsEncoder() {
        return BATCH_WS_MS_ENCODER;
    }
}
