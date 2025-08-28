package org.playmore.game.util;

import cn.hutool.core.util.ReflectUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import org.playmore.common.pb.ProtoData;
import org.playmore.common.util.CheckNull;
import org.playmore.game.domain.entity.annotation.AssociateType;
import org.playmore.game.domain.entity.function.FunctionType;
import org.playmore.game.domain.entity.role.DbRole;
import org.playmore.game.domain.entity.role.RoleFunctionData;
import org.playmore.pb.CommonPb;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @ClassName DbUtil
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:18
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:18
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class DbUtil {

    /**
     * 获取功能数据
     *
     * @param dbRole   db角色
     * @param funcType 功能类型
     * @return 功能数据
     * @throws IllegalAccessException         抛出异常
     * @throws InvalidProtocolBufferException 抛出异常
     */
    public static CommonPb.FunctionClientBase getFunctionBase(DbRole dbRole,
                                                              FunctionType funcType)
            throws IllegalAccessException, InvalidProtocolBufferException {
        for (Field field : ReflectUtil.getFieldMap(DbRole.class).values()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!RoleFunctionData.class.isAssignableFrom(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            Object obj = field.get(dbRole);
            for (Field subField : ReflectUtil.getFieldMap(obj.getClass()).values()) {
                if (Modifier.isStatic(subField.getModifiers())) {
                    continue;
                }
                AssociateType at = subField.getAnnotation(AssociateType.class);
                if (at == null || at.value() == null) {
                    continue;
                }
                if (at.value() != funcType) {
                    continue;
                }
                subField.setAccessible(true);
                byte[] data = (byte[]) subField.get(obj);
                if (CheckNull.isEmpty(data)) {
                    continue;
                }

                return CommonPb.FunctionClientBase.parseFrom(data, ProtoData.getRegistry());
            }
        }

        return null;
    }
}
