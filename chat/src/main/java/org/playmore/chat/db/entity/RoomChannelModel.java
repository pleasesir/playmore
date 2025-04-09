package org.playmore.chat.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Date;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 14:52
 */
@Getter
@Setter
@TableName(value = "room_channel", autoResultMap = true)
public class RoomChannelModel extends Model<RoomChannelModel> {
    @Serial
    private static final long serialVersionUID = 3283186435118536902L;
    @TableId(type = IdType.ASSIGN_ID)
    private Long uid;
    /**
     * 聊天室ID
     */
    private long roomId;
    /**
     * 频道号
     */
    private int chlId;
    /**
     * 频道内聊天记录最多保存记录数目
     */
    private int saveChatCount;
    private Date lastModifyTime;

    public RoomChannelModel(long roomId, int chlId) {
        this.roomId = roomId;
        this.chlId = chlId;
        this.lastModifyTime = new Date();
        this.saveChatCount = 150;
    }

    public RoomChannelModel() {
    }

    public RoomChannelModel setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
        return this;
    }
}
