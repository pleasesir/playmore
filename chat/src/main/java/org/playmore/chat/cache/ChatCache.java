package org.playmore.chat.cache;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.playmore.chat.cache.component.AbstractTimerCache;
import org.playmore.chat.cache.component.ICacheVO;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.cache.vo.ChannelMapVO;
import org.playmore.chat.cache.vo.ChatMsgShardingCacheVO;
import org.playmore.chat.cache.vo.ChatPrivateRoomMapVO;
import org.playmore.chat.cache.vo.RoomMapVO;
import org.playmore.chat.db.entity.ProviderConfigModel;
import org.playmore.chat.db.service.impl.ChatMsgServiceImpl;
import org.playmore.chat.db.service.impl.ChatPrivateRoomServiceImpl;
import org.playmore.chat.db.service.impl.ChatRoomChannelServiceImpl;
import org.playmore.chat.db.service.impl.ChatRoomServiceImpl;
import org.playmore.chat.db.service.impl.ProviderConfigConfigServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.playmore.chat.cache.CacheKeyRule.CACHE_KEY_SPLIT_CHAR;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-11-08 10:43
 */
@Component
public class ChatCache extends AbstractTimerCache<String, ICacheVO> {
    @Autowired
    private ChatRoomServiceImpl chatRoomService;
    @Autowired
    private ChatRoomChannelServiceImpl roomChannelService;
    @Autowired
    private ChatMsgServiceImpl chatMsgService;
    @Autowired
    private ChatPrivateRoomServiceImpl chatPrivateRoomService;
    @Autowired
    private ProviderConfigConfigServiceImpl providerConfigService;
    @Value("${persistInterval}")
    protected int persistInterval;

    @Getter
    private static ChatCache instance;

    @Override
    @PostConstruct
    public void init() {
        super.init(persistInterval);
        instance = this;
    }

    @SuppressWarnings("unchecked")
    public SimpleCacheVO<ProviderConfigModel> getProviderConfig(long chatServerProviderId) {
        String cacheKey = CacheKeyRule.getCacheKey(CacheKeyRule.CHAT_SERVER_PROVIDER_CONFIG, chatServerProviderId);
        return (SimpleCacheVO<ProviderConfigModel>) getProperty(cacheKey, false);
    }

    /**
     * 获取所有有效的房间列表
     *
     * @return 房间列表
     */
    public RoomMapVO getAllChatRoom() {
        String cacheKey = CacheKeyRule.getCacheKey(CacheKeyRule.ALL_CHAT_ROOM);
        return (RoomMapVO) getProperty(cacheKey, false);
    }

    public ChannelMapVO getChannelMapVO(long chatRoomId) {
        String cacheKey = CacheKeyRule.getCacheKey(CacheKeyRule.CHAT_ROOM_CHANNEL, chatRoomId);
        return (ChannelMapVO) getProperty(cacheKey, false);
    }

    public ChatPrivateRoomMapVO getPrivateChatRoom(long roleId) {
        String cacheKey = CacheKeyRule.getCacheKey(CacheKeyRule.PRIVATE_CHAT_ROOM, roleId);
        return (ChatPrivateRoomMapVO) getProperty(cacheKey, false);
    }

//    /**
//     * 根据频道唯一ID查找该频道内的玩家列表
//     *
//     * @param chlId
//     * @return
//     */
//    public MemberMapVO getMemberMapVO(long chatRoomId, int chlId) {
//        String cacheKey = CacheKeyRule.getCacheKey(CacheKeyRule.CHANNEL_MEMBERS, chatRoomId, chlId);
//        return (MemberMapVO) getProperty(cacheKey, false);
//    }
//
//
//    public Map<Integer, MemberMapVO> getMemberMapVO(long roomId, Collection<Integer> chlList) {
//        List<String> cacheKeys = chlList.stream()
//                .map(chlId -> CacheKeyRule.getCacheKey(CacheKeyRule.CHANNEL_MEMBERS, roomId, chlId))
//                .collect(Collectors.toList());
//        Map<String, ? extends ICacheVO> rtMap = getPropertyList(false, cacheKeys);
//        Map<Integer, MemberMapVO> chlMemberMap = new HashMap<>();
//        for (Map.Entry<String, ? extends ICacheVO> entry : rtMap.entrySet()) {
//            int chlId = Integer.parseInt(entry.getKey().split(CACHE_KEY_SPLIT_CHAR)[2]);
//            chlMemberMap.put(chlId, (MemberMapVO) entry.getValue());
//        }
//        return chlMemberMap;
//    }

    public ChatMsgShardingCacheVO getChatMsgVO(long roomId, int chlId, int count) {
        String cacheKey = CacheKeyRule.getCacheKey(CacheKeyRule.CHANNEL_CHAT_MSG, roomId, chlId, count);
        return (ChatMsgShardingCacheVO) getProperty(cacheKey, false);
    }

    public ChatMsgShardingCacheVO getPrivateMsgVO(long roomId, int count) {
        String cacheKey = CacheKeyRule.getCacheKey(CacheKeyRule.PRIVATE_CHAT_MSG, roomId, count);
        return (ChatMsgShardingCacheVO) getProperty(cacheKey, false);
    }

    @Override
    protected ICacheVO findProperty(String cacheKey) {
        String[] keyRules = cacheKey.split(CACHE_KEY_SPLIT_CHAR);
        switch (keyRules[0]) {
            case CacheKeyRule.CHAT_SERVER_PROVIDER_CONFIG: {
                return providerConfigService.selectProviderConfig(Integer.parseInt(keyRules[1]));
            }
            case CacheKeyRule.CHAT_ROOM_CHANNEL: {
                return roomChannelService.selectRoomChannelVO(Long.parseLong(keyRules[1]));
            }
            case CacheKeyRule.CHANNEL_CHAT_MSG: {
                long roomId = Long.parseLong(keyRules[1]);
                int channelId = Integer.parseInt(keyRules[2]);
                int count = Integer.parseInt(keyRules[3]);
                ChatMsgShardingCacheVO vo = chatMsgService.selectChatMsgVO(roomId, channelId, count);
                vo.setCacheKey(cacheKey);
                return vo;
            }
            case CacheKeyRule.ALL_CHAT_ROOM: {
                return chatRoomService.selectValidRoom();
            }
            case CacheKeyRule.CHANNEL_MEMBERS: {
                return null;
            }
            case CacheKeyRule.PRIVATE_CHAT_ROOM:
                long roleId = Long.parseLong(keyRules[1]);
                return chatPrivateRoomService.selectPrivateChatRoomMap(roleId);
            case CacheKeyRule.PRIVATE_CHAT_MSG:
                long roomId = Long.parseLong(keyRules[1]);
                int count = Integer.parseInt(keyRules[2]);
                ChatMsgShardingCacheVO vo = chatMsgService.selectPrivateMsgVO(roomId, count);
                vo.setCacheKey(cacheKey);
                return vo;
            default:
                return null;
        }
    }

    @Override
    protected Map<String, ICacheVO> findPropertyMap(List<String> keyList) {
        String key = keyList.get(0);
        String[] keyRules = key.split(CACHE_KEY_SPLIT_CHAR);
        switch (keyRules[0]) {
            case CacheKeyRule.CHANNEL_MEMBERS: {
                return null;
            }
            default:
                return null;
        }
    }

    public ConcurrentHashMap<String, ICacheVO>[] caches() {
        return caches;
    }

    @Override
    public Map<String, ICacheVO> removeSpecifyTypeCache(String keyType, Object... params) {
        return null;
    }

}
