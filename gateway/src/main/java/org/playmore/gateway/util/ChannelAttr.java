package org.playmore.gateway.util;

import cn.hutool.crypto.symmetric.AES;
import io.netty.util.AttributeKey;
import org.playmore.common.msg.ChannelId;

/**
 * @ClassName ChannelAttr
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 00:12
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 00:12
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class ChannelAttr {
    public static AttributeKey<Long> ROLE_ID = AttributeKey.valueOf("roleId");
    public static AttributeKey<ChannelId> CHANNEL_ID = AttributeKey.valueOf("channelId");
    public static AttributeKey<Long> rqTime = AttributeKey.valueOf("rqMills");
    public static AttributeKey<Integer> GAME_SERVER_ID = AttributeKey.valueOf("game_server_id");
    public static AttributeKey<Boolean> GAME_SERVER_SYNC_OFFLINE = AttributeKey.valueOf("game_server_sync_offline");
    public static AttributeKey<AES> SECRET_ENCODER_DECODER = AttributeKey.valueOf("secret_encoder_decoder");
}
