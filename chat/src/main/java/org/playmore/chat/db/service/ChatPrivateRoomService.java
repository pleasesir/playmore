package org.playmore.chat.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.playmore.chat.cache.vo.ChatPrivateRoomMapVO;
import org.playmore.chat.db.entity.ChatPrivateRoomModel;

/**
 * @Author: zhangpeng
 * @Date: 2023/10/16/20:34
 * @Description:
 */
public interface ChatPrivateRoomService extends IService<ChatPrivateRoomModel> {
    /**
     * 查询私聊房间
     *
     * @param roleId
     * @return
     */
    ChatPrivateRoomMapVO selectPrivateChatRoomMap(long roleId);
}
