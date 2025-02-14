package org.playmore.chat.db.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.playmore.chat.constant.DBConstant;
import org.playmore.chat.db.entity.ChatMsgModel;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 20:22
 */
@Mapper
@DS(DBConstant.DATASOURCE_SHARDING_CENTER)
public interface ChatMsgMapper extends BaseMapper<ChatMsgModel> {

    /**
     * 删除时间跨服过长聊天
     *
     * @param expiredTime
     * @return
     */
    @Delete("delete from `chat_msg` where `chat_time` < #{expiredTime}")
    int deleteExpiredMsg(@Param("expiredTime") long expiredTime);

    /**
     * 删除聊天信息
     *
     * @param roomId
     * @param msgId
     * @return
     */
    @Delete("delete from `chat_msg` where `room_id` = #{roomId} and `unique_msg_id` = #{msgId}")
    int deleteByRoomAndMsgId(@Param("roomId") long roomId, @Param("msgId") long msgId);
}
