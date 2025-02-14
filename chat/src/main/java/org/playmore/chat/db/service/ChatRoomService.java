package org.playmore.chat.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.playmore.chat.cache.vo.RoomMapVO;
import org.playmore.chat.db.entity.ChatRoomModel;

import java.util.Date;
import java.util.Map;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 16:00
 */
public interface ChatRoomService extends IService<ChatRoomModel> {
    /**
     * 查找所有在有效期内的房间
     *
     * @return 房间信息
     */
    RoomMapVO selectValidRoom();

    /**
     * entity.expiredTime > expiredTime 的房间列表
     *
     * @param expiredTime 房间的过期时间
     * @return 有效期内的房间列表
     */
    Map<Integer, Map<Long, ChatRoomModel>> selectRooms(Date expiredTime);


}
