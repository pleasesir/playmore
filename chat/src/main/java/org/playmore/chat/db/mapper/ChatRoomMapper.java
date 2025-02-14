package org.playmore.chat.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.playmore.chat.db.entity.ChatRoomModel;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 16:46
 */
public interface ChatRoomMapper extends BaseMapper<ChatRoomModel> {
    /**
     * 查询当前最大的聊天室id
     *
     * @return
     */
    @Select("SELECT max(chat_room_id) as maxId FROM chat_room")
    Integer selectMaxChatRoomId();
}
