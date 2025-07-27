package org.playmore.chat.cache.vo;

import lombok.Setter;
import lombok.ToString;
import org.playmore.chat.cache.component.AbsMajorVO;
import org.playmore.chat.cache.component.PersistContext;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.config.AppContext;
import org.playmore.chat.db.entity.ChatMsgModel;
import org.playmore.chat.db.mapper.ChatMsgMapper;
import org.playmore.common.util.CheckNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * @Author: zhangpeng
 * @Date: 2023/10/11/16:43
 * @Description:
 */
@ToString
public class ChatMsgShardingCacheVO extends AbsMajorVO<Long, ChatMsgModel> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 最大长度
     */
    private final Integer capacity;
    @Setter
    private String cacheKey;

    public ChatMsgShardingCacheVO(int capacity) {
        super(new ConcurrentSkipListMap<>());
        this.capacity = capacity;
    }

    @Override
    public void put(Long msgId, SimpleCacheVO<ChatMsgModel> simpleCacheVO) {
        super.put(msgId, simpleCacheVO);
        if (capacity > 0 && data.size() > capacity) {
            ConcurrentSkipListMap<Long, SimpleCacheVO<ChatMsgModel>> dataMap = (ConcurrentSkipListMap<Long,
                    SimpleCacheVO<ChatMsgModel>>) data;
            while (data.size() > capacity) {
                super.remove(dataMap.firstKey());
            }
        }
    }

    public SimpleCacheVO<ChatMsgModel> getLastMsg() {
        if (data == null || data.isEmpty()) {
            return null;
        }
        ConcurrentSkipListMap<Long, SimpleCacheVO<ChatMsgModel>> dataMap = (ConcurrentSkipListMap<Long, SimpleCacheVO<ChatMsgModel>>) data;
        return dataMap.lastEntry().getValue();
    }


    public List<ChatMsgModel> getAllChatMsgEntities() {
        if (data.size() > 0) {
            return data.values().stream()
                    .filter(svo -> svo.getModel() != null)
                    .map(SimpleCacheVO::getModel)
                    .collect(Collectors.toList());
        }
        return null;
    }


    public void doRoomExpired(long roomId) {
        for (Map.Entry<Long, SimpleCacheVO<ChatMsgModel>> entry : getUnmodifiableMap().entrySet()) {
            ChatMsgModel model = entry.getValue().getModel();
            if (Objects.nonNull(model) && model.getRoomId() == roomId) {
                remove(model.getUniqueMsgId());
            }
        }
    }

    @Override
    protected void delete(Object key, PersistContext ctx) {
        persistLock.lock();
        try {
            if (CheckNull.isEmpty(delSet)) {
                return;
            }
            Set<SimpleCacheVO<ChatMsgModel>> fixRmSet = new HashSet<>();
            boolean delException = false;
            ChatMsgMapper chatMsgMapper = Objects.requireNonNull(AppContext.getBean(ChatMsgMapper.class));
            for (SimpleCacheVO<ChatMsgModel> deleteModel : delSet) {
                ChatMsgModel model = deleteModel.getModel();
                Serializable pkVal = model != null ? model.pkVal() : null;
                if (pkVal == null) {
                    fixRmSet.add(deleteModel);
                } else {
                    try {
                        chatMsgMapper.deleteByRoomAndMsgId(model.getRoomId(), model.getUniqueMsgId());
                        fixRmSet.add(deleteModel);
                        ctx.getDeleteCount().incrementAndGet();
                    } catch (Exception e) {
                        delException = true;
                        logger.error("缓存KEY :{}, 删除 pk :{}, 失败!!!, model :{}", key, pkVal, model, e);
                    }
                }
            }
            if (!fixRmSet.isEmpty()) {
                for (SimpleCacheVO<ChatMsgModel> deleteModel : fixRmSet) {
                    delSet.remove(deleteModel);
                }
            }
            if (delException) {
                expiryTime += ctx.getTtl();
            }
        } finally {
            persistLock.unlock();
        }
    }

    public String cacheKey() {
        return cacheKey;
    }

}
