package org.playmore.game.domain.entity.role;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import org.playmore.common.util.LogUtil;
import org.playmore.game.domain.entity.annotation.AssociateType;
import org.playmore.game.domain.entity.function.FunctionType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName RoleFunctionData
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:19
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:19
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class RoleFunctionData {
    private static final Map<FunctionType, Field> FIELD_OFFSET_MAP = new HashMap<>(FunctionType.values().length);

    public RoleFunctionData() {
        load(this.getClass());
    }

    public <T extends RoleFunctionData> void load(Class<T> clazz) {
        if (RoleFunctionData.FIELD_OFFSET_MAP.size() == FunctionType.values().length) {
            return;
        }

        Set<FunctionType> types = new HashSet<>();
        for (Field field : ReflectUtil.getFieldMap(clazz).values()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null && !tableField.exist()) {
                continue;
            }
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                continue;
            }
            AssociateType associateType = field.getAnnotation(AssociateType.class);
            if (associateType == null) {
                throw new IllegalStateException("@AssociateType is required, name: " + field.getName());
            }
            if (associateType.value() == null) {
                throw new IllegalStateException("@AssociateType value is null, name: " + field.getName());
            }
            FunctionType functionType = associateType.value();
            if (!types.add(functionType)) {
                throw new IllegalStateException("@AssociateType is repeated, func: " + functionType);
            }

            field.setAccessible(true);
            RoleFunctionData.FIELD_OFFSET_MAP.put(functionType, field);
        }
    }

    public static Field getField(FunctionType ft) {
        return RoleFunctionData.FIELD_OFFSET_MAP.get(ft);
    }

    public static void setFieldData(DbRole dbRole, FunctionType ft, byte[] data) {
        Field field = RoleFunctionData.getField(ft);
        if (field != null) {
            try {
                Class<?> clazz = field.getDeclaringClass();
                for (Field origField : ReflectUtil.getFieldMap(DbRole.class).values()) {
                    if (origField.getType() == clazz) {
                        Object obj = origField.get(dbRole);
                        if (obj == null) {
                            obj = ReflectUtil.newInstance(clazz, dbRole.getRoleId());
                            origField.setAccessible(true);
                            origField.set(dbRole, obj);
                        }
                        dbRole.setNeedSave(true);
                        field.set(obj, data);
                    }
                }
            } catch (IllegalAccessException e) {
                LogUtil.error(e);
            }
        }
    }
}
