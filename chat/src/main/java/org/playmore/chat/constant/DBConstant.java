package org.playmore.chat.constant;

/**
 * @program: chat
 * @description:
 * @author: zhou jie
 * @create: 2020-05-22 11:42
 */
public class DBConstant {

    /**
     * 数据源分组 - 中心服db
     */
    public static final String DATASOURCE_CENTER = "centerDB";

    /**
     * 数据源分组 - 中心服-分表-db
     */
    public static final String DATASOURCE_SHARDING_CENTER = "center-sharding-db";

    /**
     * 数据源分组 - 配置库
     */
    public static final String DATASOURCE_INI = "iniDB";

    public static final byte CHAT_MSG_CLEAR_MONTH_LIMIT = -1;

    public static final byte CLEAR_CHAT_MSG_HOUR_OF_DAY = 5;
}
