package org.playmore.gateway.net.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.Setter;
import org.playmore.common.util.CheckNull;
import org.playmore.gateway.component.GateExecutorComponent;
import org.playmore.gateway.net.CmdFlag;
import org.playmore.gateway.net.codec.msg.WsMessagePackage;
import org.playmore.gateway.net.work.ConfirmWork;
import org.playmore.gateway.util.ChannelUtil;

import java.util.List;

/**
 * @ClassName ConfirmDecoder
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 23:06
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 23:06
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@ChannelHandler.Sharable
@Setter
public class ConfirmDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    private GateExecutorComponent executor;

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) {
        ByteBuf msg = frame.content();
        int total = msg.readInt();
        byte flag = msg.readByte();
        int cmdId = msg.readInt();

        switch (flag) {
            case CmdFlag.CONFIRM_CMD:
                int serverId = msg.readInt();
                byte[] bytes = new byte[total - 9];
                msg.readBytes(bytes);
                executor.
                        publishNoOrderReceiveTask(new ConfirmWork(new WsMessagePackage(flag, bytes, serverId), ctx));
                ctx.fireChannelReadComplete();
                break;
            case CmdFlag.FIRST_GAME_MSG:
                bytes = new byte[total - 5];
                msg.readBytes(bytes);
                out.add(new WsMessagePackage(flag, bytes));
                break;
            case CmdFlag.HEART_BEAT:
                // 字段总长度 + 标识位 + 秒数长度
                ByteBuf buf;
                int totalLen = 17;
                buf = ctx.channel().alloc().heapBuffer(totalLen);
                buf.writeInt(totalLen);
                buf.writeByte(CmdFlag.HEART_BEAT);
                buf.writeInt(0);
                buf.writeLong(System.currentTimeMillis() / 1000L);
                BinaryWebSocketFrame wsFrame = new BinaryWebSocketFrame(buf);
                ctx.writeAndFlush(wsFrame);
                ctx.fireChannelReadComplete();
                break;
            case CmdFlag.NORMAL_MSG:
                if (CheckNull.isEmpty(ChannelUtil.getRoleId(ctx))) {
                    ChannelUtil.closeChannel(ctx, ", beginGame消息还未完成");
                    return;
                }
                bytes = new byte[total - 5];
                msg.readBytes(bytes);
                out.add(new WsMessagePackage(flag, bytes).setCmdId(cmdId));
                break;
            default:
                ChannelUtil.closeChannel(ctx, "发送的协议没有加标识, flag: " + flag);
                break;
        }
    }
}
