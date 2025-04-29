package org.playmore.api.verticle.eventbus.event.impl;


import org.playmore.api.verticle.eventbus.event.Address;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-30 0:06
 * @description TODO
 */
public enum DatabaseEvent implements Address {
    NONE(),
    /**
     * 操作动态数据库
     */
    DYNAMIC_OPERATE_DB(),
    /**
     * 收集查询动态数据库结果集
     */
    DYNAMIC_COLLECT_DB(),
    /**
     * 动态库批量插入
     */
    DYNAMIC_BATCH_INSERT(),


    /**
     * ini配置数据库操作
     */
    INI_OPERATE_DB(),
    /**
     * 收集查询ini配置数据库结果集
     */
    INIT_COLLECT_DB(),
    /**
     * ini配置数据库批量插入
     */
    INI_BATCH_INSERT(),
}
