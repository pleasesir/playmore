package org.playmore.chat.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.playmore.chat.cache.vo.ChatMsgShardingCacheVO;
import org.playmore.chat.cache.vo.ChatMsgShardingModel;
import org.playmore.chat.db.entity.ChatMsgModel;
import org.playmore.chat.db.mapper.ChatMsgMapper;
import org.playmore.chat.db.service.ChatMsgService;
import org.playmore.common.util.CheckNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 20:25
 */
@Service
public class ChatMsgServiceImpl extends ServiceImpl<ChatMsgMapper, ChatMsgModel> implements ChatMsgService {

    @Override
    public ChatMsgShardingCacheVO selectChatMsgVO(long roomId, int channelId, int capacity) {
        QueryWrapper<ChatMsgModel> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("room_id", roomId)
                .eq("chl_id", channelId)
                .orderByDesc("uid")
                .last("limit " + capacity);
        List<ChatMsgModel> rtList = baseMapper.selectList(queryWrapper);
        ChatMsgShardingCacheVO vo = new ChatMsgShardingCacheVO(capacity);
        if (CheckNull.nonEmpty(rtList)) {
            for (ChatMsgModel entity : rtList) {
                ChatMsgShardingModel simpleCacheVO = new ChatMsgShardingModel(entity);
                vo.put(entity.getUniqueMsgId(), simpleCacheVO);
            }
        }
        return vo;
    }

    @Override
    public ChatMsgShardingCacheVO selectPrivateMsgVO(long roomId, int count) {
        QueryWrapper<ChatMsgModel> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("room_id", roomId)
                .orderByDesc("uid")
                .last("limit " + count);
        List<ChatMsgModel> rtList = baseMapper.selectList(queryWrapper);
        ChatMsgShardingCacheVO vo = new ChatMsgShardingCacheVO(count);
        if (CheckNull.nonEmpty(rtList)) {
            for (ChatMsgModel entity : rtList) {
                ChatMsgShardingModel simpleCacheVO = new ChatMsgShardingModel(entity);
                vo.put(entity.getUniqueMsgId(), simpleCacheVO);
            }
        }
        return vo;
    }
}
