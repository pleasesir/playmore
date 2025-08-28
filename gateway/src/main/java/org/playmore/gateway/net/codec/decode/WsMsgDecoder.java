package org.playmore.gateway.net.codec.decode;

import cn.hutool.crypto.symmetric.AES;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.playmore.api.config.AppContext;
import org.playmore.common.msg.impl.GatewayMsg;
import org.playmore.common.util.CheckNull;
import org.playmore.gateway.component.GateServerComponent;
import org.playmore.gateway.net.codec.msg.WsMessagePackage;
import org.playmore.gateway.util.ChannelUtil;
import org.playmore.gateway.util.CodecUtil;

import java.util.List;

/**
 * @ClassName WsDecoder
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 00:16
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 00:16
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@ChannelHandler.Sharable
public class WsMsgDecoder extends MessageToMessageDecoder<WsMessagePackage> {

    @Override
    protected void decode(ChannelHandlerContext ctx, WsMessagePackage msg, List<Object> out) {
        byte[] bytes = msg.getBody();
        AES aes = ChannelUtil.getEncoder(ctx);
        if (CheckNull.nonEmpty(aes)) {
            bytes = CodecUtil.decrypt(bytes, aes);
        }

        GatewayMsg clientMessage = GatewayMsg.createMsg(ChannelUtil.getChannelId(ctx),
                ChannelUtil.getRoleId(ctx), bytes);
        clientMessage.setFromServerId(AppContext.getBean(GateServerComponent.class).getGatewayId());

        clientMessage.setCmdFlag(msg.getCmdFlag());
        out.add(clientMessage);
    }
}
