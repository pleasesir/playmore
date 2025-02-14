package org.playmore.chat.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.playmore.chat.cache.vo.ChannelMapVO;
import org.playmore.chat.db.entity.RoomChannelModel;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 19:54
 */
public interface ChatRoomChannelService extends IService<RoomChannelModel> {

    /**
     * 查询房间频道
     *
     * @param chatRoomId
     * @return
     */
    ChannelMapVO selectRoomChannelVO(long chatRoomId);
}
