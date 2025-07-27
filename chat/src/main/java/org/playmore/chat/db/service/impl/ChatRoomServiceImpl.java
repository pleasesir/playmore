package org.playmore.chat.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.cache.vo.RoomMapVO;
import org.playmore.chat.db.entity.ChatRoomModel;
import org.playmore.chat.db.mapper.ChatRoomMapper;
import org.playmore.chat.db.service.ChatRoomService;
import org.playmore.common.util.CheckNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 16:47
 */
@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomModel> implements ChatRoomService {

    @Override
    public Map<Integer, Map<Long, ChatRoomModel>> selectRooms(Date expiredTime) {
        QueryWrapper<ChatRoomModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("expired_time", expiredTime);
        List<ChatRoomModel> rtList = baseMapper.selectList(queryWrapper);
        Map<Integer, Map<Long, ChatRoomModel>> typeMap = new ConcurrentHashMap<>();
        if (CheckNull.nonEmpty(rtList)) {
            for (ChatRoomModel entity : rtList) {
                Map<Long, ChatRoomModel> roomMap = typeMap.computeIfAbsent(entity.getRoomType(), k -> new ConcurrentHashMap<>());
                roomMap.put(entity.getRoomId(), entity);
            }
        }
        return typeMap;
    }

    @Override
    public RoomMapVO selectValidRoom() {
        QueryWrapper<ChatRoomModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("expired_time", 0);
        queryWrapper.or(wrapper -> wrapper.gt("expired_time", System.currentTimeMillis()));
        List<ChatRoomModel> rtList = baseMapper.selectList(queryWrapper);
        RoomMapVO vo = new RoomMapVO();
        if (CheckNull.nonEmpty(rtList)) {
            for (ChatRoomModel entity : rtList) {
                SimpleCacheVO<ChatRoomModel> simpleCacheVO = new SimpleCacheVO<>();
                simpleCacheVO.setModel(entity);
                vo.put(entity.getRoomId(), simpleCacheVO);
            }
        }
        return vo;
    }

    public SimpleCacheVO<ChatRoomModel> selectChatRoom(int roomType, int chatRoomId) {
        QueryWrapper<ChatRoomModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chat_room_id", chatRoomId)
                .eq("room_type", roomType);
        ChatRoomModel chatRoom = baseMapper.selectOne(queryWrapper);
        SimpleCacheVO<ChatRoomModel> simpleCacheVO = new SimpleCacheVO<>();
        if (Objects.nonNull(chatRoom)) {
            simpleCacheVO.setModel(chatRoom);
        }
        return simpleCacheVO;
    }

}
