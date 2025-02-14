package org.playmore.chat.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-27 10:58
 */
@Setter
@Getter
@TableName("provider_config")
public class ProviderConfigModel extends Model<ProviderConfigModel> {
    @Serial
    private static final long serialVersionUID = 2998369741828139463L;
    @TableId
    private Long providerId;
    private int maxRoomId;

    public int incMaxRoomId() {
        return ++maxRoomId;
    }

    public ProviderConfigModel(long providerId) {
        this.providerId = providerId;
    }

}
