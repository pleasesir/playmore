package org.playmore.api.util;

import com.google.protobuf.GeneratedMessage;
import org.playmore.api.exception.GameError;

/**
 * @ClassName CommonPbHelper
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/6/17 22:32
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/6/17 22:32
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class CommonPbHelper {


    public static BasePb.Base createRsBase(int cmd, int code) {
        BasePb.Base.Builder builder = BasePb.Base.newBuilder();
        builder.setCmd(cmd).setCode(code);
        return builder.build();
    }

    public static <T> BasePb.Base.Builder createRsBase(int cmd, GeneratedMessage.GeneratedExtension<BasePb.Base, T> ext,
                                               T rsPb) {
        BasePb.Base.Builder builder = BasePb.Base.newBuilder();
        builder.setCmd(cmd).setCode(GameError.OK.getCode()).setExtension(ext, rsPb);
        return builder;
    }

}
