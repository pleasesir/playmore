package org.playmore.chat.db.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;
import org.playmore.chat.db.handler.ListIntTypeHandler;
import org.playmore.chat.db.handler.LongDateTypeHandler;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 聊天房间
 *
 * @Description
 * @Author zhangdh
 * @Date 2021-12-01 15:10
 */
@Getter
@Setter
@TableName(value = "chat_room", autoResultMap = true)
public class ChatRoomModel extends Model<ChatRoomModel> {
    @Serial
    private static final long serialVersionUID = -5625008560173054564L;
    /**
     * 房间ID
     */
    @Getter
    @TableId(type = IdType.ASSIGN_ID)
    private Long roomId;
    /**
     * 房间类型
     */
    private int roomType;
    /**
     * 房间创建者
     */
    private long createId;
    /**
     * 公共频道ID
     */
    private int pubChannelId;
    private long maxMsgId;
    @Setter
    @TableField(exist = false)
    private AtomicLong maxMsgCounter;
    private int maxChlId;
    /**
     * 创建时间
     */
    @TableField(value = "create_time",
            updateStrategy = FieldStrategy.ALWAYS,
            insertStrategy = FieldStrategy.ALWAYS,
            jdbcType = JdbcType.TIMESTAMP,
            typeHandler = LongDateTypeHandler.class)
    private long createTime;
    /**
     * 房间过期时间, 0-永不过期
     */
    @TableField(value = "expired_time",
            updateStrategy = FieldStrategy.ALWAYS,
            insertStrategy = FieldStrategy.ALWAYS,
            jdbcType = JdbcType.TIMESTAMP,
            typeHandler = LongDateTypeHandler.class)
    private long expiredTime;

    /**
     * 最后更新时间
     */
    @TableField(value = "last_modify_time",
            updateStrategy = FieldStrategy.ALWAYS,
            insertStrategy = FieldStrategy.ALWAYS,
            jdbcType = JdbcType.TIMESTAMP,
            typeHandler = LongDateTypeHandler.class)
    private long lastModifyTime;
    /**
     * 参与的服务器id
     */
    @TableField(value = "server_id",
            updateStrategy = FieldStrategy.ALWAYS,
            insertStrategy = FieldStrategy.ALWAYS,
            jdbcType = JdbcType.VARCHAR,
            typeHandler = ListIntTypeHandler.class)
    private List<Integer> serverId;
    private long mapUniqueId;

    public ChatRoomModel(long roomId, int roomType, long createId, long expiredTime) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.createId = createId;
        this.expiredTime = expiredTime;
        createTime = System.currentTimeMillis();
        lastModifyTime = createTime;
    }

    public ChatRoomModel() {
    }

    public long incMaxMsgId() {
        return maxMsgCounter.incrementAndGet();
    }

    public synchronized int incMaxChlId() {
        return ++maxChlId;
    }

    public long getMaxMsgId() {
        if (maxMsgCounter == null) {
            return maxMsgId;
        }
        return maxMsgCounter.get();
    }

    public void setMaxMsgId(long maxMsgId) {
        this.maxMsgId = maxMsgId;
        maxMsgCounter = new AtomicLong(maxMsgId);
    }

    public ChatRoomModel setServerId(List<Integer> serverId) {
        this.serverId = serverId;
        return this;
    }

    public ChatRoomModel setMapUniqueId(long mapUniqueId) {
        this.mapUniqueId = mapUniqueId;
        return this;
    }
}
