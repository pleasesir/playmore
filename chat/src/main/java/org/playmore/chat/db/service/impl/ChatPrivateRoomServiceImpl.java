package org.playmore.chat.db.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.cache.vo.ChatPrivateRoomMapVO;
import org.playmore.chat.db.entity.ChatPrivateRoomModel;
import org.playmore.chat.db.mapper.ChatPrivateRoomMapper;
import org.playmore.chat.db.service.ChatPrivateRoomService;
import org.playmore.chat.util.CheckNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: zhangpeng
 * @Date: 2023/10/16/20:35
 * @Description:
 */
@Service
public class ChatPrivateRoomServiceImpl extends ServiceImpl<ChatPrivateRoomMapper, ChatPrivateRoomModel> implements ChatPrivateRoomService {

    @Override
    public ChatPrivateRoomMapVO selectPrivateChatRoomMap(long roleId) {
        List<ChatPrivateRoomModel> list = baseMapper.selectPrivateRoom(roleId, DateUtil.offsetMonth(new Date(), -4).getTime());
        ChatPrivateRoomMapVO mapVO = new ChatPrivateRoomMapVO();
        if (CheckNull.nonEmpty(list)) {
            for (ChatPrivateRoomModel model : list) {
                if (model == null) {
                    continue;
                }

                SimpleCacheVO<ChatPrivateRoomModel> cacheVO = new SimpleCacheVO<>();
                cacheVO.setModel(model);
                if (roleId == model.getSmallerRoleId()) {
                    mapVO.put(model.getBiggerRoleId(), cacheVO);
                } else {
                    mapVO.put(model.getSmallerRoleId(), cacheVO);
                }
            }
        }
        return mapVO;
    }
}
