package org.playmore.gateway.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @ClassName GatewayServerConfig
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 00:08
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 00:08
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Getter
@Configuration
public class GatewayServerConfig {
    @Value("${environment}")
    private String environment;
    @Value("${gateway.port}")
    private int port;

    @Value("${gateway.ssl.enabled}")
    private boolean sslEnable;
    @Value("${gateway.ssl.path}")
    private String sslPath;
    @Value("${gateway.ssl.certificate.chain.file}")
    private String certificateFile;
    @Value("${gateway.ssl.private.key.file}")
    private String privateKeyFile;

    /**
     * 是否是线上环境
     *
     * @return true 线上环境
     */
    public boolean isReleaseEnv() {
        return Objects.nonNull(environment) && "release".equalsIgnoreCase(environment);
    }
}
