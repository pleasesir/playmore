package org.playmore.common.msg;

import org.playmore.common.util.LogUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public record ChannelId(String channelId, int gatewayServerId) implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 7089472473653904670L;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChannelId(String id, int serverId))) {
            return false;
        }
        return gatewayServerId() == serverId && Objects.equals(channelId(), id);
    }

    @Override
    public int hashCode() {
        return channelId.hashCode();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            LogUtil.error("channelId clone failure, e: ", e);
        }
        return null;
    }
}
