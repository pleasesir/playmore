package org.playmore.api.config;

import org.apache.dubbo.config.ServiceConfigBase;
import org.apache.dubbo.config.context.ModuleConfigManager;
import org.apache.dubbo.config.spring.util.DubboBeanUtils;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.playmore.api.listener.AppStartedEvent;
import org.playmore.common.util.LogUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.Nullable;

/**
 * @ClassName ExportServiceListener
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/31 23:41
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/31 23:41
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class ExportServiceListener implements ApplicationListener<AppStartedEvent> {
    @Override
    public void onApplicationEvent(@Nullable AppStartedEvent event) {
        try {
            ApplicationModel applicationModel = DubboBeanUtils.getApplicationModel(AppContext.getContext());
            if (applicationModel == null) {
                LogUtil.error("applicationModel is null");
                return;
            }

            ModuleConfigManager config = applicationModel.getDefaultModule().getConfigManager();
            for (ServiceConfigBase<?> service : config.getServices()) {
                //服务已发布
                if (service.isExported()) {
                    continue;
                }
                service.setExport(true);
                service.export();
                LogUtil.start(String.format("----------dubbo服务注册成功--------- : %s", service));
            }
        } catch (Exception ex) {
            LogUtil.error(ex);
            System.exit(1);
        }
    }
}
