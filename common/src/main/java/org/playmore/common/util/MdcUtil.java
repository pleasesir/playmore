package org.playmore.common.util;


import cn.hutool.core.lang.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-25 22:42
 * @description TODO
 */
public class MdcUtil {
    public static final String TRACE_ID = "trace_id";

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID);
        if (StringUtils.isNotEmpty(traceId)) {
            return traceId;
        }

        return UUID.fastUUID().toString();
    }
}
