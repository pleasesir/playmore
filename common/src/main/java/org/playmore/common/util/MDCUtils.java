package org.playmore.common.util;

import cn.hutool.core.lang.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * @ClassName MDCUtils
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:41
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:41
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class MDCUtils {
    public static final String TRACE_ID = "trace_id";
    public static final String QUEUE_UNIQUE_ID = "queue_unique_id";

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID);
        if (StringUtils.isNotEmpty(traceId)) {
            return traceId;
        }

        return UUID.fastUUID().toString();
    }
}
