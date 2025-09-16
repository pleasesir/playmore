package org.playmore.chat.db.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;
import org.playmore.chat.db.handler.ListStringTypeHandler;
import org.playmore.chat.db.handler.MapStringTypeHandler;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 19:16
 */
@Getter
@TableName(value = "chat_msg", autoResultMap = true)
public class ChatMsgModel extends Model<ChatMsgModel> implements Comparable<ChatMsgModel> {
    @Serial
    private static final long serialVersionUID = 3372452899482145598L;
    @Setter
    @TableId(type = IdType.ASSIGN_ID)
    private Long uid;

    /**
     * 房间ID
     */
    @Setter
    private long roomId;
    /**
     * 聊天室频道ID
     */
    @Setter
    private int chlId;
    /**
     * 房间内消息ID
     */
    @Setter
    private long msgId;
    /**
     * 消息唯一id
     */
    private long uniqueMsgId;
    /**
     * 聊天类型, 1-玩家消息, 2-系统消息
     */
    @Setter
    private int msgType;
    /**
     * 聊天样式(0-默认, 1-大喇叭)
     */
    @Setter
    private int msgStyle;
    /**
     * 聊天内容
     */
    @Setter
    private String msgContent;
    private Integer templateId;
    /**
     * 玩家发言: 代表玩家信息 等级, 头像框...
     * 系统公告: 代表公告参数
     */
    @Setter
    @TableField(updateStrategy = FieldStrategy.ALWAYS,
            insertStrategy = FieldStrategy.ALWAYS,
            typeHandler = MapStringTypeHandler.class)
    private Map<String, String> msgParam;

    @Setter
    @TableField(updateStrategy = FieldStrategy.ALWAYS,
            insertStrategy = FieldStrategy.ALWAYS,
            typeHandler = ListStringTypeHandler.class)
    private List<String> extParam;
    /**
     * 聊天时间
     */
    @Setter
    private long chatTime;

    public ChatMsgModel setTemplateId(Integer templateId) {
        this.templateId = templateId;
        return this;
    }

    public ChatMsgModel setUniqueMsgId(long uniqueMsgId) {
        this.uniqueMsgId = uniqueMsgId;
        return this;
    }

    @Override
    public int compareTo(ChatMsgModel o) {
        return Long.compare(this.msgId, o.msgId);
    }
}
