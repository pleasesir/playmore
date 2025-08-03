package org.playmore.game.domain.db.dao;

import lombok.Data;
import org.playmore.common.util.CheckNull;
import org.playmore.common.util.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName ServerConfig
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:22
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:22
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "treasure.event.group")
public class ServerConfig {
    private int globalEventGroupNum;
    private int globalEventBufferSize;
    private int playerEventGroupNum;
    private int playerEventBufferSize;
    private int asyncMessageEventThreadNum;
    private int kafkaRingBufferSize;
    private int kafkaRingBufferConsumerSize;
    @Value("${treasure.game.server.id}")
    private int serverId;
    private String environment = "test";
    private int rpcPort;
    @Value("${spring.application.name}")
    private String applicationName;
    private List<Integer> slaveServerIdList;
    private Date openTime;
    /**
     * gm开关
     */
    @Value("#{T(java.lang.Boolean).parseBoolean('${gmFlag:}')}")
    private boolean gmFlag;
    /**
     * 禁止创角
     */
    private byte forbidCreateRole;

    public void init() {
        if (openTime == null) {
            throw new IllegalStateException("服务器未在账号服配置, 当前服务器ID: " + serverId);
        }
    }

    /**
     * 是否是线上环境
     *
     * @return true 线上环境
     */
    public boolean isReleaseEnv() {
        return Objects.nonNull(environment) && "release".equalsIgnoreCase(environment);
    }

    public synchronized void setSlaveServerIdList(List<Integer> serverIdList) {
        if (CheckNull.isEmpty(serverIdList)) {
            return;
        }
        if (slaveServerIdList != null) {
            LogUtil.error("从服信息已经初始化过了, cur: ", slaveServerIdList);
        }

        slaveServerIdList = serverIdList;
    }

    public boolean isForbidCreateRole() {
        return forbidCreateRole == 1;
    }
}
