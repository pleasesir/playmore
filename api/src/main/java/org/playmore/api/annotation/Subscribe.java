package org.playmore.api.annotation;


import org.playmore.api.verticle.eventbus.event.impl.DatabaseEvent;
import org.playmore.api.verticle.eventbus.event.impl.GameEvent;
import org.playmore.api.verticle.eventbus.event.impl.GmEvent;

import java.lang.annotation.*;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 23:17
 * @description TODO
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Subscribe {

    GmEvent[] gmEvent() default {};

    DatabaseEvent[] dbEvent() default {};
//
//    /**
//     * 聊天事件
//     *
//     * @return 聊天事件枚举
//     */
//    ChatEvent[] chatEvent() default NONE;
//
//    /**
//     * 数据库事件
//     *
//     * @return 数据库事件枚举
//     */
//    DatabaseEvent[] dbEvent() default DatabaseEvent.NONE;
//
//    /**
//     * http 事件
//     *
//     * @return http 事件枚举
//     */
//    HttpEvent[] httpEvent() default HttpEvent.NONE;
//
//    /**
//     * manager rpc 事件
//     *
//     * @return manager rpc 事件枚举
//     */
//    ManagerEvent[] managerEvent() default ManagerEvent.NONE;
//
//    /**
//     * rank rpc 事件
//     *
//     * @return rank rpc 事件枚举
//     */
//    RankEvent[] rankEvent() default RankEvent.NONE;
//
//    /**
//     * world rpc 事件
//     *
//     * @return world rpc 事件枚举
//     */
//    WorldEvent[] worldEvent() default WorldEvent.NONE;
//
//    /**
//     * 任务事件
//     *
//     * @return 任务事件枚举
//     */
//    TaskEvent[] taskEvent() default TaskEvent.NONE;
//
//    /**
//     * 配置事件
//     *
//     * @return 配置事件枚举
//     */
//    ConfigEvent[] configEvent() default ConfigEvent.NONE;

    /**
     * 游戏服事件
     *
     * @return 游戏服事件枚举
     */
    GameEvent[] gameEvent() default GameEvent.NONE;
//
//    /**
//     * gm事件
//     *
//     * @return gm事件枚举数组
//     */
//    GmEvent[] gmEvent() default GmEvent.NONE;

    /**
     * 是否需要唯一地址拼接
     *
     * @return 是否需要唯一地址拼接
     */
    boolean uniqueAddress() default true;
}
