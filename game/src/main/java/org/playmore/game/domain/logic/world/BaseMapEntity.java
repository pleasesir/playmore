package org.playmore.game.domain.logic.world;

import com.google.protobuf.GeneratedMessage;

/**
 * @ClassName BaseMapEntity
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/9/12 13:03
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/9/12 13:03
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface BaseMapEntity<Proto extends GeneratedMessage> {

    /**
     * 获取脏数据
     *
     * @return PB信息
     */
    Proto collectDirty();

    /**
     * 获取所有元素信息PB
     *
     * @return PB信息
     */
    Proto collectAll();
}
