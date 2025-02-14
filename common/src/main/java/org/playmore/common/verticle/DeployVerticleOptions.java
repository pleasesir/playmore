package org.playmore.common.verticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.json.JsonObject;

/**
 * 构建verticle部署选项信息
 * 默认使用虚拟线程模型
 *
 * @Author: zhangpeng
 * @Date: 2025/02/13/17:01
 * @Description:
 */
public class DeployVerticleOptions extends DeploymentOptions {
    public DeployVerticleOptions() {
        super();
        this.setThreadingModel(ThreadingModel.VIRTUAL_THREAD);
    }

    public DeployVerticleOptions(DeploymentOptions other) {
        super(other);
        this.setThreadingModel(ThreadingModel.VIRTUAL_THREAD);
    }

    public DeployVerticleOptions(JsonObject json) {
        super(json);
        this.setThreadingModel(ThreadingModel.VIRTUAL_THREAD);
    }
}
