package org.playmore.chat.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.db.entity.ProviderConfigModel;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-27 11:15
 */
public interface ProviderConfigService extends IService<ProviderConfigModel> {
    /**
     * 查询聊天服务器动态配置
     *
     * @param chatServerProviderId
     * @return
     */
    SimpleCacheVO<ProviderConfigModel> selectProviderConfig(int chatServerProviderId);
}
