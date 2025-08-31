package org.playmore.gateway.config;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import reactor.netty.ReactorNetty;

/**
 * @ClassName B
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/31 23:37
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/31 23:37
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class BeforeContextStartConfig implements SpringApplicationRunListener {

    public BeforeContextStartConfig(SpringApplication application, String[] args) {
    }

    @Override
    public void starting(ConfigurableBootstrapContext context) {
        System.setProperty(ReactorNetty.IO_WORKER_COUNT, "1");
        System.setProperty(ReactorNetty.IO_SELECT_COUNT, "1");
    }
}
