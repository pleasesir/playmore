package org.playmore.chat.cache.vo;


import org.playmore.chat.cache.component.AbsMajorVO;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.db.entity.RoomChannelModel;

import java.util.Map;
import java.util.Objects;

/**
 * 房间内渠道信息
 *
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 18:18
 */
public class ChannelMapVO extends AbsMajorVO<Integer, RoomChannelModel> {

    public void doRoomExpired(long roomId) {
        for (Map.Entry<Integer, SimpleCacheVO<RoomChannelModel>> entry : data.entrySet()) {
            RoomChannelModel channel = entry.getValue().getModel();
            if (Objects.nonNull(channel) && channel.getRoomId() == roomId) {
                remove(channel.getChlId());
            }
        }
    }
}
