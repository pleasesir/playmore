package org.playmore.chat.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.cache.vo.ChannelMapVO;
import org.playmore.chat.db.entity.RoomChannelModel;
import org.playmore.chat.db.mapper.ChatRoomChannelMapper;
import org.playmore.chat.db.service.ChatRoomChannelService;
import org.playmore.chat.util.CheckNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 19:54
 */
@Service
public class ChatRoomChannelServiceImpl extends ServiceImpl<ChatRoomChannelMapper, RoomChannelModel> implements ChatRoomChannelService {

    @Override
    public ChannelMapVO selectRoomChannelVO(long chatRoomId) {
        QueryWrapper<RoomChannelModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", chatRoomId);
        List<RoomChannelModel> rtList = baseMapper.selectList(queryWrapper);
        ChannelMapVO vo = new ChannelMapVO();
        if (CheckNull.nonEmpty(rtList)) {
            for (RoomChannelModel channel : rtList) {
                SimpleCacheVO<RoomChannelModel> simpleCacheVO = new SimpleCacheVO<>();
                simpleCacheVO.setModel(channel);
                vo.put(channel.getChlId(), simpleCacheVO);
            }
        }
        return vo;
    }
}
