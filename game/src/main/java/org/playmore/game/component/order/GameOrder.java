package org.playmore.game.component.order;

/**
 * @ClassName GameOrder
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:18
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:18
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public enum GameOrder {
    /**
     * 网络组件
     */
    NET_COMPONENT(),
    /**
     * 存储玩家组件
     */
    SAVE_PLAYER(),
    /**
     * 存储公共数据组件
     */
    SAVE_GLOBAL(),
    /**
     * kafka消息队列
     */
    KAFKA_MQ(),
}
