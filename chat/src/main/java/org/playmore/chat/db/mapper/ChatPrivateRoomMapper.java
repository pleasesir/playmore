package org.playmore.chat.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.playmore.chat.db.entity.ChatPrivateRoomModel;

import java.util.List;

/**
 * @Author: zhangpeng
 * @Date: 2023/10/16/14:32
 * @Description:
 */
public interface ChatPrivateRoomMapper extends BaseMapper<ChatPrivateRoomModel> {
    /**
     * 查询私聊房间
     *
     * @param roleId
     * @param limitTime
     * @return
     */
    @Select("select `uid`," +
            "private_chat_room_id," +
            "smaller_role_id," +
            "bigger_role_id," +
            "smaller_role_server_id," +
            "bigger_role_server_id," +
            "smaller_post_last," +
            "max_msg_id," +
            "save_msg_count," +
            "last_msg," +
            "last_msg_time," +
            "smaller_role_camp," +
            "bigger_role_camp," +
            "bigger_nickname," +
            "smaller_nickname," +
            "smaller_portrait," +
            "smaller_portrait_frame," +
            "bigger_portrait," +
            "bigger_portrait_frame," +
            "smaller_msg_id," +
            "bigger_msg_id from chat_private_room" +
            " where (smaller_role_id = #{roleId} or bigger_role_id = #{roleId}) and last_msg_time > #{limitTime}")
    List<ChatPrivateRoomModel> selectPrivateRoom(long roleId, long limitTime);
}
