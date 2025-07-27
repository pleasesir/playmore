package org.playmore.chat.cache.vo;

import lombok.ToString;
import org.playmore.chat.cache.component.AbsMajorVO;
import org.playmore.chat.cache.component.PersistContext;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.db.entity.ChatPrivateRoomModel;
import org.playmore.common.util.CheckNull;

import java.util.Map;
import java.util.Objects;

/**
 * 玩家聊天室map
 *
 * @Author: zhangpeng
 * @Date: 2023/10/16/18:35
 * @Description:
 */
@ToString
public class ChatPrivateRoomMapVO extends AbsMajorVO<Long, ChatPrivateRoomModel> {
    @Override
    public void doStateCheck(Object key, PersistContext ctx) {
        //先处理删除数据
        if (CheckNull.nonEmpty(delSet)) {
            delete(key, ctx);
        }
        for (Map.Entry<Long, SimpleCacheVO<ChatPrivateRoomModel>> entry : data.entrySet()) {
            SimpleCacheVO<ChatPrivateRoomModel> simpleCacheVO = entry.getValue();
            simpleCacheVO.setExpiryTime(expiryTime);
            ChatPrivateRoomModel model;
            if (Objects.nonNull(model = simpleCacheVO.getModel()) && model.getBiggerRoleId() == entry.getKey()) {
                // 只使用更小玩家id下的房间聊天信息更新数据
                simpleCacheVO.doStateCheck(key, ctx);
            }
            if (simpleCacheVO.getExpiryTime() > expiryTime) {
                expiryTime = simpleCacheVO.getExpiryTime();
            }
        }
    }
}
