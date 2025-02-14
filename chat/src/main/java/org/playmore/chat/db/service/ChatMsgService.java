package org.playmore.chat.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.playmore.chat.cache.vo.ChatMsgShardingCacheVO;
import org.playmore.chat.db.entity.ChatMsgModel;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 20:23
 */
public interface ChatMsgService extends IService<ChatMsgModel> {

    /**
     * 获取房间中指定频道的聊天记录
     *
     * @param roomId   房间ID
     * @param chlId    频道UID
     * @param capacity 查询数量
     * @return 聊天记录
     */
    ChatMsgShardingCacheVO selectChatMsgVO(long roomId, int chlId, int capacity);

    /**
     * 获取私聊房间中指定频道的聊天记录
     *
     * @param roomId
     * @param count
     * @return
     */
    ChatMsgShardingCacheVO selectPrivateMsgVO(long roomId, int count);
}
