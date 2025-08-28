package org.playmore.gateway.net.codec.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.playmore.common.msg.impl.GatewayMsg;
import org.playmore.gateway.util.ChannelUtil;
import org.playmore.gateway.util.CodecUtil;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @ClassName WsEncoder
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 00:15
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 00:15
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@ChannelHandler.Sharable
public class WsMsgEncoder extends MessageToMessageEncoder<GatewayMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, GatewayMsg msg, List<Object> out) throws NoSuchAlgorithmException {
        ByteBuf buf = CodecUtil.encode(ctx, msg.getBody(), msg.getCmdId(), ChannelUtil.getEncoder(ctx));
        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(buf);
        out.add(binaryWebSocketFrame);
    }
}
