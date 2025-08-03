package org.playmore.common.msg.impl;

import lombok.Getter;
import lombok.Setter;
import org.playmore.common.msg.BaseRpcMsg;
import org.playmore.common.msg.ChannelId;

import java.io.Serial;

/**
 * @ClassName GatewayMsg
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:24
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:24
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Getter
@Setter
public class GatewayMsg extends BaseRpcMsg {
    @Serial
    private static final long serialVersionUID = 1392053796469648153L;
    /**
     * 客户端唯一id
     */
    protected ChannelId seqId;
    /**
     * 消息通知的关闭状态
     */
    protected Byte closeStatus;
    /**
     * 协议类型
     */
    protected Byte cmdFlag;
    /**
     * 协议id
     */
    private int cmdId;

    public GatewayMsg() {
    }

    /**
     * 服务器关闭通知
     *
     * @param serverId 服务器id
     * @return 创建的网关消息
     */
    public static GatewayMsg serverClose(int serverId, byte status) {
        GatewayMsg msg = new GatewayMsg();
        msg.closeStatus = status;
        msg.fromServerId = serverId;
        return msg;
    }

    /**
     * 客户端关闭通知
     *
     * @param seqId
     * @return
     */
    public static GatewayMsg clientClose(ChannelId seqId, byte status) {
        GatewayMsg msg = new GatewayMsg();
        msg.seqId = seqId;
        msg.closeStatus = status;
        return msg;
    }

    /**
     * 创建网关请求消息
     *
     * @param seqId        sessionId
     * @param roleId       玩家id
     * @param fromServerId 服务器来源id
     * @param body
     * @return
     */
    public static GatewayMsg createRqMsg(ChannelId seqId, Long roleId, int fromServerId, byte[] body) {
        GatewayMsg msg = new GatewayMsg();
        msg.seqId = seqId;
        msg.roleId = roleId;
        msg.body = body;
        msg.fromServerId = fromServerId;
        return msg;
    }

    public static GatewayMsg createMsg(ChannelId seqId, byte[] body) {
        GatewayMsg msg = new GatewayMsg();
        msg.body = body;
        msg.seqId = seqId;
        return msg;
    }

    public static GatewayMsg createMsg(ChannelId seqId, Long roleId, byte[] body) {
        GatewayMsg msg = new GatewayMsg();
        msg.roleId = roleId;
        msg.body = body;
        msg.seqId = seqId;
        return msg;
    }

    @Override
    public long uniqueId() {
        return 0;
    }
}
