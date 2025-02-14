package org.playmore.chat.cache.component;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.List;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-07-08 14:31
 */
public interface ICacheStoreService {
    void deleteBatch(Class<?> cls, List<? extends Serializable> idsList);

    void deleteBatch(BaseMapper<?> mapper, List<? extends Serializable> idsList);

    void updateEntityList(List<Model<?>> updateList);

    void insertEntityList(List<Model<?>> insertList);

    void mergeEntityList(List<Model<?>> mergeList);
}
