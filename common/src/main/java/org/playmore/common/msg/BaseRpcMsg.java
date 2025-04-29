package org.playmore.common.msg;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 22:55
 * @description TODO
 */
@Getter
@Setter
public abstract class BaseRpcMsg implements Serializable {
    @Serial
    private static final long serialVersionUID = 3920076977422186818L;

    /**
     * 玩家id
     */
    protected Long roleId;
    /**
     * 请求的消息体
     */
    protected byte[] body;
    /**
     * 消息发送方服务器id
     */
    protected Integer fromServerId;

    /**
     * 消息唯一id
     *
     * @return 消息唯一ID
     */
    public abstract long uniqueId();
}
