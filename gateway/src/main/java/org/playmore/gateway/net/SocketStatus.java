package org.playmore.gateway.net;

/**
 * @ClassName SocketStatus
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/29 00:28
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/29 00:28
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface SocketStatus {
    /**
     * 网关 -> 通知游戏服 网关下线
     */
    byte GATEWAY_SERVER_CLOSING = 1;
    /**
     * 网关 -> 通知游戏服 客户端断开
     */
    byte CLIENT_CLOSING = 2;
    /**
     * 游戏服 -> 通知网关 游戏服下线
     */
    byte GAME_SERVER_CLOSING = 3;
    /**
     * 游戏服 -> 通知网关断开 客户端
     */
    byte GAME_CLOSE_CLIENT = 4;
}
