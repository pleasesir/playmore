package org.playmore.game.domain.entity.function;

import lombok.Getter;
import org.springframework.core.Ordered;

/**
 * @ClassName FunctionType
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 22:56
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 22:56
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Getter
public enum FunctionType {

    ,
    ;

    private final int initialPriority;
    
    private final Class<? extends BaseFunctionEntity<?>> clazz;

    FunctionType(int initialPriority, Class<? extends BaseFunctionEntity<?>> clazz) {
        this.initialPriority = initialPriority;
        this.clazz = clazz;
    }

    FunctionType(Class<? extends BaseFunctionEntity<?>> clazz) {
        this.clazz = clazz;
        this.initialPriority = Ordered.LOWEST_PRECEDENCE;
    }
}
