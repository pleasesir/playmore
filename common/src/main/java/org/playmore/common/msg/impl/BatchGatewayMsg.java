package org.playmore.common.msg.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.playmore.common.msg.BaseRpcMsg;
import org.playmore.common.msg.ChannelId;

import java.io.Serial;
import java.util.Set;

/**
 * @ClassName BatchGatewayMsg
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:38
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:38
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BatchGatewayMsg extends BaseRpcMsg {
    @Serial
    private static final long serialVersionUID = -6292291700773591933L;

    private final Set<ChannelId> channelSet;
    /**
     * 回包
     */
    private final int cmdId;

    public BatchGatewayMsg(Set<ChannelId> channelSet, int cmdId) {
        this.channelSet = channelSet;
        this.cmdId = cmdId;
    }

    @Override
    public long uniqueId() {
        return StringUtils.joinWith(StringUtils.SPACE, this.cmdId, this.fromServerId).hashCode();
    }
}
