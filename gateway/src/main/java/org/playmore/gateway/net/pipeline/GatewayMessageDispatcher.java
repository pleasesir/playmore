package org.playmore.gateway.net.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.playmore.api.config.AppContext;
import org.playmore.api.rpc.game.GameServerRpcService;
import org.playmore.common.constant.DubboConst;
import org.playmore.common.msg.ChannelId;
import org.playmore.common.msg.impl.GatewayMsg;
import org.playmore.common.util.CheckNull;
import org.playmore.common.util.LogUtil;
import org.playmore.gateway.bootstrap.GateServerBootstrap;
import org.playmore.gateway.component.GateExecutorComponent;
import org.playmore.gateway.component.GateNetComponent;
import org.playmore.gateway.component.GateServerComponent;
import org.playmore.gateway.net.BaseWebsocketServer;
import org.playmore.gateway.net.CmdFlag;
import org.playmore.gateway.net.SocketStatus;
import org.playmore.gateway.net.work.ForwardGameWork;
import org.playmore.gateway.util.ChannelUtil;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName GatewayMessageDispatcher
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/29 00:08
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/29 00:08
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class GatewayMessageDispatcher extends SimpleChannelInboundHandler<GatewayMsg> {
    /**
     * 远端连接断开
     */
    private static final String CONNECT_RESET_BY_PEER = "Connection reset by peer";
    private final GateNetComponent server;
    private final GameServerRpcService gameServerRpcService;
    private final GateServerComponent gatewayServer;
    private final GateExecutorComponent executor;
    private final GateServerBootstrap bootstrap;

    public GatewayMessageDispatcher(GateNetComponent server) {
        this.server = server;
        gameServerRpcService = AppContext.getBean(GameServerRpcService.class);
        gatewayServer = AppContext.getBean(GateServerComponent.class);
        executor = AppContext.getBean(GateExecutorComponent.class);
        bootstrap = AppContext.getBean(GateServerBootstrap.class);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, GatewayMsg msg) {
        server.maxMessage.incrementAndGet();
        ChannelId channelId;
        if ((channelId = ChannelUtil.getChannelId(ctx)) == null ||
                CheckNull.isEmpty(ChannelUtil.getGameServerId(ctx))) {
            // 不是第一个协议, 但是没记录channelId, 协议请求异常, 关闭当前channel
            ChannelUtil.closeChannel(ctx, "channelId或服务器id为空, 请求的开始游戏还未完成(channelId: " +
                    ChannelUtil.getChannelId(ctx) + ")" + ", serverId: " + ChannelUtil.getGameServerId(ctx));
            return;
        }

        // 检测消息是否发送过快
        if (msg.getCmdFlag() == CmdFlag.FIRST_GAME_MSG && ChannelUtil.rqTooFast(ctx)) {
            LogUtil.error(String.format("clientId: %s, send beginGame msg too fast", ChannelUtil.getChannelId(ctx)));
            return;
        }

        // 异步校验后RPC调用
        if (msg.getCmdFlag() == CmdFlag.FIRST_GAME_MSG) {
            ChannelUtil.setRqTime(ctx);
        }

        // 转发到游戏服
        executor.publishSendTask(channelId.hashCode(),
                new ForwardGameWork(ChannelUtil.getGameServerId(ctx), msg));
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (server.isAliHealthCheckAddress(ctx)) {
            return;
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (server.isAliHealthCheckAddress(ctx)) {
            return;
        }
        super.channelUnregistered(ctx);
        if (ChannelUtil.getChannelId(ctx) != null) {
            LogUtil.common(String.format("MessageHandler channelUnregistered : %s", ctx.channel().remoteAddress()));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (server.isAliHealthCheckAddress(ctx)) {
            return;
        }
        super.channelActive(ctx);
        int total = server.maxConnect.get();
        if (total > BaseWebsocketServer.MAX_CONNECT) {
            LogUtil.common("MessageHandler channelActive : " + ctx.channel().remoteAddress(), " 当前连接数过多!");
            ChannelUtil.closeChannel(ctx, "连接数过多(" + total + ")");
            return;
        } else {
            total = server.maxConnect.incrementAndGet();
            Channel channel = ctx.channel();
            LogUtil.common(String.format("玩家与网关之间的连接激活 %s -> %s, 当前总连接数 :%s",
                    channel.remoteAddress(), channel.localAddress(), total));
        }

        // 延迟检查玩家建立连接后, 是否发送确认消息
        if (gatewayServer.getCheckConnectInterval() > 0) {
            ctx.channel().eventLoop().schedule(() -> {
                ChannelId channelId = ChannelUtil.getChannelId(ctx);
                if (channelId == null) {
                    // 玩家未发送确认消息
                    ChannelUtil.closeChannel(ctx, null);
                }
            }, gatewayServer.getCheckConnectInterval(), TimeUnit.SECONDS);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try {
            Channel channel = ctx.channel();
            ChannelId channelId = ChannelUtil.getChannelId(channel);
            server.disConnect(channelId);
            if (server.isAliHealthCheckAddress(ctx)) {
                return;
            }
            super.channelInactive(ctx);
            int total = server.maxConnect.decrementAndGet();
            if (channelId != null) {
                LogUtil.common(String.format("roleId :%s, 玩家与网关之间的连接断开 %s -> %s, 剩余总连接数 :%s",
                        ChannelUtil.getRoleId(ctx), channel.remoteAddress(), channel.localAddress(), total));
            }
            // 通知游戏服客户端断开连接
            notifyGameServer(ctx);
        } catch (Exception e) {
            LogUtil.error("", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        if (server.isAliHealthCheckAddress(ctx)) {
            ctx.close();
            return;
        }
        String reason = StringUtils.EMPTY;
        try {
            Long roleId;
            Throwable throwable;
            Throwable ex = (throwable = t.getCause()) == null ? t : throwable;
            reason = ex.getMessage();
            boolean printError = !(ex instanceof IOException) || StringUtils.isEmpty(ex.getMessage())
                    || !CONNECT_RESET_BY_PEER.equalsIgnoreCase(ex.getMessage());
            boolean sslExp = ex instanceof SSLException;
            if (printError || sslExp) {
                if (sslExp) {
                    LogUtil.WARN_LOGGER.warn("MessageHandler exceptionCaught!!! {} {}",
                            ctx, (roleId = ChannelUtil.getRoleId(ctx)) == null ? ChannelUtil.getChannelId(ctx) : roleId, t);
                } else {
                    LogUtil.ERROR_LOGGER.error("MessageHandler exceptionCaught!!! {} {}",
                            ctx, (roleId = ChannelUtil.getRoleId(ctx)) == null ? ChannelUtil.getChannelId(ctx) : roleId, t);
                }
            }
        } finally {
            ChannelUtil.closeChannel(ctx, reason);
            notifyGameServer(ctx);
        }
    }

    /**
     * 通知游戏服客户端断开连接
     *
     * @param ctx ChannelHandlerContext
     */
    private void notifyGameServer(ChannelHandlerContext ctx) {
        if (bootstrap.isStarted()) {
            // 通知游戏服客户端断开连接
            Integer gameServerId = ChannelUtil.getGameServerId(ctx);
            ChannelId seqId = ChannelUtil.getChannelId(ctx);
            Long roleId = ChannelUtil.getRoleId(ctx);
            if (!ChannelUtil.isGameServerSyncOffline(ctx) && seqId != null
                    && roleId != null && Objects.nonNull(gameServerId)) {
                GatewayMsg msg = GatewayMsg.clientClose(seqId, SocketStatus.CLIENT_CLOSING);
                msg.setRoleId(roleId);
                msg.setFromServerId(gatewayServer.getGatewayId());
                RpcContext.getClientAttachment().setAttachment(DubboConst.PROVIDER_ID, gameServerId);
                gameServerRpcService.cast(msg);
            }
        }
    }

}
