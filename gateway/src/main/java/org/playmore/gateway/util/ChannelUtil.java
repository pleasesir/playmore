package org.playmore.gateway.util;

import cn.hutool.crypto.symmetric.AES;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.playmore.api.config.AppContext;
import org.playmore.common.msg.ChannelId;
import org.playmore.common.util.CheckNull;
import org.playmore.common.util.LogUtil;
import org.playmore.common.util.NumberUtil;
import org.playmore.gateway.component.GateServerComponent;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @ClassName ChannelUtil
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 00:11
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 00:11
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class ChannelUtil {
    public static void closeChannel(ChannelHandlerContext ctx, String reason) {
        if (ctx == null) {
            return;
        }
        Channel channel = ctx.channel();
        LogUtil.warn(String.format("roleId :%s, 玩家channelId: %s, serverId: %s",
                        ChannelUtil.getRoleId(ctx), ChannelUtil.getChannelId(channel), ChannelUtil.getGameServerId(ctx)),
                "-->close [because] " + reason);
        AppContext.getBean(GateServerComponent.class).getGatewayNettyServer().disConnect(ChannelUtil.getChannelId(ctx));
    }

    public static void setEncoder(ChannelHandlerContext ctx, AES aes) {
        Attribute<AES> attr = ctx.channel().attr(ChannelAttr.SECRET_ENCODER_DECODER);
        attr.set(aes);
    }

    public static AES getEncoder(ChannelHandlerContext ctx) {
        return ctx.channel().attr(ChannelAttr.SECRET_ENCODER_DECODER).get();
    }

    public static void setGameServerSyncOffline(ChannelHandlerContext ctx) {
        Attribute<Boolean> attr = ctx.channel().attr(ChannelAttr.GAME_SERVER_SYNC_OFFLINE);
        attr.set(Boolean.TRUE);
    }

    public static boolean isGameServerSyncOffline(ChannelHandlerContext ctx) {
        Attribute<Boolean> attr = ctx.channel().attr(ChannelAttr.GAME_SERVER_SYNC_OFFLINE);
        if (attr == null) {
            return false;
        }
        return Boolean.TRUE.equals(attr.get());
    }

    public static Long getRoleId(ChannelHandlerContext ctx) {
        Long channelId = ctx.channel().attr(ChannelAttr.ROLE_ID).get();
        return Objects.nonNull(channelId) ? channelId : null;
    }

    public static void setRoleId(ChannelHandlerContext ctx, long roleId) {
        ctx.channel().attr(ChannelAttr.ROLE_ID).set(roleId);
    }

    public static ChannelId getChannelId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(ChannelAttr.CHANNEL_ID).get();
    }

    public static void setChannelId(ChannelHandlerContext ctx, ChannelId id) {
        setChannelId(ctx.channel(), id);
    }

    public static ChannelId createChannelId(ChannelHandlerContext ctx) {
        return createChannelId(ctx.channel());
    }

    public static ChannelId createChannelId(Channel channel) {
        return new ChannelId(channel.id().asLongText(), AppContext.getBean(GateServerComponent.class).getGatewayId());
    }

    public static void setChannelId(Channel channel, ChannelId channelId) {
        Attribute<ChannelId> attribute = channel.attr(ChannelAttr.CHANNEL_ID);
        attribute.set(channelId);
    }

    public static ChannelId getChannelId(Channel channel) {
        Attribute<ChannelId> attr = channel.attr(ChannelAttr.CHANNEL_ID);
        return attr.get();
    }

    public static Integer getGameServerId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(ChannelAttr.GAME_SERVER_ID).get();
    }

    public static void setGameServerId(ChannelHandlerContext ctx, int serverId) {
        ctx.channel().attr(ChannelAttr.GAME_SERVER_ID).set(serverId);
    }

    public static String getIp(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return null;
        }

        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        return address.getAddress().getHostAddress();
    }

    public static void setRqTime(ChannelHandlerContext ctx) {
        Attribute<Long> attribute = ctx.channel().attr(ChannelAttr.rqTime);
        if (CheckNull.isEmpty(attribute)) {
            attribute.set(System.currentTimeMillis());
        }
    }

    public static void removeRqTime(ChannelHandlerContext ctx) {
        ctx.channel().attr(ChannelAttr.rqTime).set(null);
    }

    public static Long getLastRqTime(ChannelHandlerContext ctx) {
        return ctx.channel().attr(ChannelAttr.rqTime).get();
    }

    public static boolean rqTooFast(ChannelHandlerContext ctx) {
        Long lastRqTime = getLastRqTime(ctx);
        if (CheckNull.isEmpty(lastRqTime)) {
            return false;
        }
        return System.currentTimeMillis() - lastRqTime <= 3 * NumberUtil.THOUSAND;
    }
}

