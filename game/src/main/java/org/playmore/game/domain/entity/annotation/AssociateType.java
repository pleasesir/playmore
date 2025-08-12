package org.playmore.game.domain.entity.annotation;

import org.playmore.game.domain.entity.function.FunctionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName AssociateType
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:21
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:21
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AssociateType {

    FunctionType value();

}
