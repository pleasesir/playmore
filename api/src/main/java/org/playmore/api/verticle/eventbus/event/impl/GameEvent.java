package org.playmore.api.verticle.eventbus.event.impl;

import org.playmore.api.verticle.eventbus.event.Address;

/**
 * @ClassName GameEvent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:00
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:00
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public enum GameEvent implements Address {

    NONE(),

    SERVER_START(false),

    EXC_PLAYER_EVENT(),

    GET_PLAYER_DETAIL_PB(),


    GET_PLAYER_DETAIL(),

    SYNC_PLAYER_PB(),

    ROLE_ACROSS_DAY(),

    ROLE_CONFIG_RELOAD(),

    GATEWAY_SERVER_OFFLINE(),
    ;

    private boolean uniqueAddress = true;

    GameEvent() {
    }

    GameEvent(boolean uniqueAddress) {
        this.uniqueAddress = uniqueAddress;
    }

    @Override
    public boolean uniqueAddress() {
        return uniqueAddress;
    }
}
