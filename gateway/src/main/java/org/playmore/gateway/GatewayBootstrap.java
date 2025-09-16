package org.playmore.gateway;

import org.apache.logging.log4j.status.StatusLogger;
import org.playmore.api.config.AppContext;
import org.playmore.gateway.bootstrap.GateServerBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName GatewayBootstrap
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/4 22:31
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/4 22:31
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@SpringBootApplication
public class GatewayBootstrap {
    public static void main(String[] args) {
        try {
            SpringApplication.run(GatewayBootstrap.class, args);
            AppContext.getBean(GateServerBootstrap.class).init();
        } catch (Exception e) {
            StatusLogger.getLogger().error(e);
            System.exit(1);
        }
    }
}
