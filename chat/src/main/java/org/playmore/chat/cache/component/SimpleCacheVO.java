package org.playmore.chat.cache.component;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.playmore.chat.util.CRC32Util;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * 映射数据库中一条计入
 *
 * @Description
 * @Author zhangdh
 * @Date 2021-07-21 15:36
 */
@Slf4j
@Getter
public class SimpleCacheVO<T extends Model<?>> extends StoredCacheVO {
    protected volatile T model;

    public SimpleCacheVO() {
    }

    /**
     * 设置model并设置 insert 状态
     */
    public SimpleCacheVO(T model) {
        this.model = model;
    }

    public T getIfAbsent(Supplier<T> fn) {
        if (model == null) {
            synchronized (this) {
                if (model == null) {
                    model = fn.get();
                }
            }
        }
        return model;
    }

    @Override
    public boolean isChange() {
        return rawValue != CRC32Util.calcCRC32Value(model);
    }

    /**
     * @param key 缓存key
     * @param ctx 本次持久化ctx
     */
    @Override
    public void doStateCheck(Object key, PersistContext ctx) {
        if (Objects.isNull(model)) {
            return;
        }
        long startNano = System.nanoTime();
        long crc32Value = CRC32Util.calcCRC32Value(model);
        long costNano = System.nanoTime() - startNano;
        ctx.addSerCostTime(costNano);
        //缓存过期被清除时入库一次防止CRC32碰撞
        if (crc32Value != rawValue || (ctx.getStartTime() >= expiryTime)) {
            startNano = System.nanoTime();
            persist(key, ctx);
            costNano = System.nanoTime() - startNano;
            ctx.addPersistTime(costNano);
            rawValue = crc32Value;
        }
    }

    protected synchronized void persist(Object key, PersistContext ctx) {
        try {
            Serializable pkVal = model.pkVal();
            if (Objects.isNull(pkVal)) {
                model.insert();
                ctx.getInsertCount().incrementAndGet();
            } else {
                if (!model.updateById()) {
                    model.insert();
                }
                ctx.getUpdateCount().incrementAndGet();
            }
            storedCount++;
        } catch (Exception e) {
            if (expiryTime - ctx.getStartTime() <= ctx.getTtl()) {
                expiryTime += ctx.getTtl();
            }
            List<Model<?>> saveList = ctx.getSaveMap().computeIfAbsent(model.getClass(), t -> new CopyOnWriteArrayList<>());
            saveList.add(model);
        }
    }

    /**
     * 此方法只能将数据库中已有的数据查询出来后放入, 不能将在内存中生成的对象直接放入.
     * 如果需要将在内存中new 出来的对象放入此中, 请使用 {@link #getIfAbsent}
     *
     * @param model 无状态的对象
     */
    public void setModel(T model) {
        if (model == null) {
            return;
        }
        synchronized (this) {
            if (Objects.isNull(model.pkVal())) {
                throw new IllegalArgumentException("只能存入已经生成过pk的对象");
            }
            this.model = model;
            rawValue = CRC32Util.calcCRC32Value(model);
        }
    }

    @Override
    public String toString() {
        return "SimpleCacheVO{" +
                "model=" + model +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleCacheVO<?> that)) {
            return false;
        }
        return Objects.equals(getModel(), that.getModel());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getModel());
    }
}
