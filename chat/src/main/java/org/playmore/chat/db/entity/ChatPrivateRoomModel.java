package org.playmore.chat.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 玩家私聊房间
 *
 * @Author: zhangpeng
 * @Date: 2023/10/13/17:05
 * @Description:
 */
@Getter
@TableName(value = "chat_private_room", autoResultMap = true)
public class ChatPrivateRoomModel extends Model<ChatPrivateRoomModel> {
    @Serial
    private static final long serialVersionUID = -1818785998587867178L;
    @TableId(type = IdType.ASSIGN_ID)
    private Long uid;
    /**
     * 私聊聊天室id
     */
    @Setter
    private long privateChatRoomId;
    /**
     * 更小的玩家id
     */
    @Setter
    private long smallerRoleId;
    /**
     * 更大的玩家id
     */
    @Setter
    private long biggerRoleId;
    /**
     * 更小玩家id的服务器id
     */
    @Setter
    private int smallerRoleServerId = -1;
    /**
     * 更大玩家id的服务器id
     */
    @Setter
    private int biggerRoleServerId = -1;
    /**
     * 更小玩家阵营
     */
    @Setter
    private int smallerRoleCamp;
    /**
     * 更大玩家阵营
     */
    @Setter
    private int biggerRoleCamp;
    /**
     * 更小玩家昵称
     */
    @Setter
    private String smallerNickname;
    /**
     * 更大玩家昵称
     */
    @Setter
    private String biggerNickname;
    /**
     * 自增聊天id
     */
    @Setter
    private int maxMsgId;
    /**
     * 更小玩家id自增聊天id
     */
    @Setter
    private int smallerMsgId;
    /**
     * 更大玩家id自增聊天id
     */
    @Setter
    private int biggerMsgId;
    /**
     * 保存信息最大条数
     */
    @Setter
    private int saveMsgCount;
    /**
     * 最后一条消息
     */
    @Setter
    private byte[] lastMsg;
    /**
     * 最后一条消息发送时间
     */
    @Setter
    private long lastMsgTime;
    @Setter
    private String smallerPortrait;
    @Setter
    private int smallerPortraitFrame;
    @Setter
    private String biggerPortrait;
    @Setter
    private int biggerPortraitFrame;

    public ChatPrivateRoomModel setUid(Long uid) {
        this.uid = uid;
        return this;
    }

    public synchronized long incMaxMsgId() {
        return ++this.maxMsgId;
    }

    public synchronized long incBiggerMsgId() {
        return ++biggerMsgId;
    }

    public synchronized long incSmallerMsgId() {
        return ++smallerMsgId;
    }

    public void transferData(ChatPrivateRoomModel otherPrivateRoom) {
        otherPrivateRoom.setLastMsg(this.getLastMsg());
        otherPrivateRoom.setLastMsgTime(this.getLastMsgTime());
        otherPrivateRoom.setSmallerNickname(this.getSmallerNickname());
        otherPrivateRoom.setSmallerRoleCamp(this.getSmallerRoleCamp());
        otherPrivateRoom.setBiggerNickname(this.getBiggerNickname());
        otherPrivateRoom.setBiggerRoleCamp(this.getBiggerRoleCamp());
        otherPrivateRoom.setSmallerPortrait(this.getSmallerPortrait());
        otherPrivateRoom.setSmallerPortraitFrame(this.getSmallerPortraitFrame());
        otherPrivateRoom.setBiggerPortrait(this.getBiggerPortrait());
        otherPrivateRoom.setBiggerPortraitFrame(this.getBiggerPortraitFrame());
        otherPrivateRoom.setSmallerMsgId(this.getSmallerMsgId());
        otherPrivateRoom.setBiggerMsgId(this.getBiggerMsgId());
        otherPrivateRoom.setPrivateChatRoomId(this.getPrivateChatRoomId());
        otherPrivateRoom.setSaveMsgCount(this.getSaveMsgCount());
        otherPrivateRoom.setBiggerRoleServerId(this.getBiggerRoleServerId());
        otherPrivateRoom.setSmallerRoleServerId(this.getSmallerRoleServerId());
        otherPrivateRoom.setMaxMsgId(this.getMaxMsgId());
        otherPrivateRoom.setSmallerRoleId(this.getSmallerRoleId());
        otherPrivateRoom.setBiggerRoleId(this.getBiggerRoleId());
    }
}
