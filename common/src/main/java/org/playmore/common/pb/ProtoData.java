package org.playmore.common.pb;

import com.google.protobuf.ExtensionRegistry;
import lombok.Getter;

/**
 * @ClassName ProtoData
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:25
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:25
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class ProtoData {
    @Getter
    private static final ExtensionRegistry registry = ExtensionRegistry.newInstance();
}
