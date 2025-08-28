package org.playmore.gateway.net.codec.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.playmore.common.msg.impl.BatchGatewayMsg;
import org.playmore.gateway.util.ChannelUtil;
import org.playmore.gateway.util.CodecUtil;

import java.util.List;

/**
 * @ClassName BatchWsMsEncoder
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 23:57
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 23:57
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class BatchWsMsEncoder extends MessageToMessageEncoder<BatchGatewayMsg> {
    @Override
    protected void encode(ChannelHandlerContext ctx, BatchGatewayMsg msg, List<Object> out) throws Exception {
        ByteBuf buf = CodecUtil.encode(ctx, msg.getBody(), msg.getCmdId(), ChannelUtil.getEncoder(ctx));
        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(buf);
        out.add(binaryWebSocketFrame);
    }
}
