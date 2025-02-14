package org.playmore.chat.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.playmore.chat.cache.component.SimpleCacheVO;
import org.playmore.chat.db.entity.ProviderConfigModel;
import org.playmore.chat.db.mapper.ProviderConfigMapper;
import org.playmore.chat.db.service.ProviderConfigService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-12-27 11:17
 */
@Service
public class ProviderConfigConfigServiceImpl extends ServiceImpl<ProviderConfigMapper, ProviderConfigModel> implements ProviderConfigService {
    @Resource
    private ProviderConfigMapper providerConfigMapper;

    @Override
    public SimpleCacheVO<ProviderConfigModel> selectProviderConfig(int chatServerProviderId) {
        QueryWrapper<ProviderConfigModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("provider_id", chatServerProviderId);
        ProviderConfigModel model = providerConfigMapper.selectOne(queryWrapper);
        SimpleCacheVO<ProviderConfigModel> cvo = new SimpleCacheVO<>();
        if (Objects.nonNull(model)) {
            cvo.setModel(model);
        }
        return cvo;
    }
}
