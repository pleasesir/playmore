package org.playmore.gateway.component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.playmore.common.msg.ChannelId;
import org.playmore.common.msg.impl.GatewayMsg;
import org.playmore.common.util.CheckNull;
import org.playmore.common.util.LogUtil;
import org.playmore.gateway.net.BaseWebsocketServer;
import org.playmore.gateway.net.codec.factory.MessageCodecFactory;
import org.playmore.gateway.util.ChannelUtil;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName GateNetComponent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/29 00:17
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/29 00:17
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class GateNetComponent extends BaseWebsocketServer implements Runnable {
    private final ConcurrentMap<ChannelId, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>();
    short beginGameRsCmd = 1102;
    private final ReentrantLock lock = new ReentrantLock();

    public GateNetComponent(int port, int rThreads, int sThreads) throws Exception {
        super(port, rThreads);
    }

    /**
     * 消息转发给玩家
     *
     * @param msg 网关消息包体
     */
    public void forwardMsg2Player(GatewayMsg msg) {
        if (msg.getSeqId() == null) {
            LogUtil.error("roleId: ", msg.getRoleId(), ", channelId is null");
            return;
        }
        ChannelHandlerContext ctx = channelMap.get(msg.getSeqId());
        if (ctx != null && ctx.channel().isActive()) {
            writeMsg(ctx, msg);
        }
    }

    private void writeMsg(ChannelHandlerContext ctx, GatewayMsg msg) {
        try {
            if (msg.getCmdId() == beginGameRsCmd &&
                    Objects.nonNull(msg.getRoleId()) && CheckNull.isEmpty(ChannelUtil.getRoleId(ctx))
                    && msg.getRoleId() > 0) {
                ChannelUtil.setRoleId(ctx, msg.getRoleId());
                ChannelUtil.removeRqTime(ctx);
            }
            ctx.channel().writeAndFlush(msg);
        } catch (Exception e) {
            LogUtil.ERROR_LOGGER.error("向客服端写入协议数据出错, e: ", e);
        }
    }

    public void addChannel(ChannelId channelId, ChannelHandlerContext ctx) {
        try {
            ChannelHandlerContext ctx0 = channelMap.put(channelId, ctx);
            if (ctx0 != null) {
                LogUtil.error("channelId :{}, new channel :{} >>>>>>>> old channel: {}", channelId, ctx, ctx0);
                ctx0.close();
            }
        } catch (Exception e) {
            LogUtil.error("", e);
        }
    }

    public void disConnect(ChannelId channelId) {
        if (channelId == null) {
            return;
        }
        try {
            ChannelHandlerContext channel = channelMap.remove(channelId);
            if (Objects.nonNull(channel)) {
                channel.close();
            }
        } catch (Exception e) {
            LogUtil.error("", e);
        }
    }

    public void disConnectServer(Integer gameServerId) {
        LogUtil.warn("刷掉下线的game Rpc服务, gameServerId: ", gameServerId);

        lock.lock();
        try {
            Iterator<ChannelHandlerContext> it = channelMap.values().iterator();
            while (it.hasNext()) {
                ChannelHandlerContext ctx = it.next();
                Integer serverId;
                if ((serverId = ChannelUtil.getGameServerId(ctx)) != null
                        && serverId.equals(gameServerId)) {
                    // 不再回调游戏服RPC下线通知
                    ChannelUtil.setGameServerSyncOffline(ctx);
                    ctx.close();
                    it.remove();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public ChannelHandlerContext getChannel(ChannelId channelId) {
        return channelMap.get(channelId);
    }

    public String getGameType() {
        return "netty-gateway";
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (Exception e) {
            LogUtil.error("网关服务器停服报错: ", e);
        }
    }

    @Override
    protected SimpleChannelInboundHandler<GatewayMsg> initGameServerHandler() {
        return MessageCodecFactory.getGatewayMessageDispatcher();
    }

    @Override
    public void run() {
        start();
        afterStart();
    }
}
