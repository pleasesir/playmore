package org.playmore.gateway.net.work;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.playmore.api.config.AppContext;
import org.playmore.api.disruptor.task.impl.AsyncTask;
import org.playmore.common.msg.ChannelId;
import org.playmore.common.util.LogUtil;
import org.playmore.gateway.component.GateServerComponent;
import org.playmore.gateway.net.CmdFlag;
import org.playmore.gateway.net.codec.msg.WsMessagePackage;
import org.playmore.gateway.util.ChannelUtil;
import org.playmore.gateway.util.CodecUtil;

/**
 * @ClassName ConfirmWork
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/29 00:06
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/29 00:06
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class ConfirmWork extends AsyncTask {
    private final WsMessagePackage msg;
    private final ChannelHandlerContext ctx;

    public ConfirmWork(WsMessagePackage msg, ChannelHandlerContext ctx) {
        super(System.currentTimeMillis());
        this.msg = msg;
        this.ctx = ctx;
    }

    private void sendMsg(String secret) {
        // 包体总长度占位 + 标记位占位 + 密钥长度占位
        byte[] secretBytes = secret.getBytes();
        int totalLen = 9 + secretBytes.length;
        ByteBuf buf = ctx.channel().alloc().buffer(totalLen);
        buf.writeInt(totalLen);
        buf.writeByte(CmdFlag.CONFIRM_CMD);
        buf.writeInt(0);
        buf.writeBytes(secretBytes);
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(buf);
        ctx.writeAndFlush(frame);
    }

    @Override
    public void action() throws Exception {
        String secret = StringUtils.EMPTY;
        try {
//            EchoService echoService = (EchoService) AppContext.getBean(GameServerRpcService.class);
//            String checkStr = GameError.OK.toString();
//            RpcContext.getClientAttachment().setAttachment(DubboConst.PROVIDER_ID, msg.getServerId());
//            if (!checkStr.equalsIgnoreCase((String) echoService.$echo(checkStr))) {
//                LogUtil.warn("当前gameRpc服务未上线, serverId: " + msg.getServerId());
//                return;
//            }

            try {
                secret = CodecUtil.confirmSecretRsa(ctx, msg.getBody());
            } catch (Exception ex) {
                secret = StringUtils.EMPTY;
                LogUtil.warn("解析token失败, ex: ", ex);
                return;
            }

            ChannelUtil.setGameServerId(ctx, msg.getServerId());
            // 开始游戏第一个协议, 记录玩家channelId
            ChannelId channelId = ChannelUtil.createChannelId(ctx);
            ChannelUtil.setChannelId(ctx, channelId);
            AppContext.getContext().getBean(GateServerComponent.class).getGatewayNettyServer().addChannel(channelId, ctx);
            LogUtil.common(String.format("玩家与网关之间发送的确认消息 %s -> %s, serverId: %s, channelId: %s",
                    ctx.channel().remoteAddress(), ctx.channel().localAddress(), msg.getServerId(), channelId));
        } catch (Throwable ex) {
            LogUtil.error("", ex);
            secret = StringUtils.EMPTY;
        } finally {
            sendMsg(secret);
        }
    }

    @Override
    protected void actionBefore(Object... args) {
    }

    @Override
    protected void onCompletion() {
    }
}
