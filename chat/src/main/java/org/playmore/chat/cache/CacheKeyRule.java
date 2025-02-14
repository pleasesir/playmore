package org.playmore.chat.cache;

import java.util.StringJoiner;

/**
 * 缓存KEY规则
 *
 * @Description
 * @Author zhangdh
 * @Date 2021-07-13 17:24
 */
public interface CacheKeyRule {
    String CACHE_KEY_SPLIT_CHAR = "_";

    /**
     * 同一个房间类型的房间信息
     */
    String CHAT_SERVER_PROVIDER_CONFIG = "1001";
    /**
     * 房间内频道信息
     */
    String CHAT_ROOM_CHANNEL = "1002";
    /**
     * 频道内的聊天内容信息
     */
    String CHANNEL_CHAT_MSG = "1003";
    /**
     * 频道内的成员信息
     */
    String CHANNEL_MEMBERS = "1004";
    /**
     * 房间列表
     */
    String ALL_CHAT_ROOM = "1005";
    /**
     * 私聊聊天室
     */
    String PRIVATE_CHAT_ROOM = "1006";
    /**
     * 频道内的私聊聊天内容信息
     */
    String PRIVATE_CHAT_MSG = "1007";


    /**
     * 拼装缓存key信息
     *
     * @param params
     * @return
     */
    static String getCacheKey(Object... params) {
        StringJoiner joiner = new StringJoiner("_");
        for (Object param : params) {
            joiner.add(param.toString());
        }
        return joiner.toString();
    }

    /**
     * 获取缓存词条对应下标含义
     *
     * @param cacheKey
     * @param index
     * @return
     */
    static String getKeyParam(String cacheKey, int index) {
        return cacheKey.split(CACHE_KEY_SPLIT_CHAR)[index];
    }
}
