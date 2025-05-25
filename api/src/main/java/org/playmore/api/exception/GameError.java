package org.playmore.api.exception;


import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 23:02
 * @description TODO
 */
@Getter
public enum GameError {
    SERVER_EXCEPTION(10, "Server exception"),
    REQUEST_RPC_SERVICE_EXCEPTION(15, "请求RPC服务异常"),
    REQUEST_TIME_OUT(16, "请求超时"),
    GAME_SERVER_MAINTENANCE(17, "游戏维护中"),
    UNKNOWN_ERROR(18, "未知错误"),
    SERVER_CONNECT_EXCEPTION(19, "服务器连接异常"),
    INVOKER_TIMEOUT(20, "服务调用超时"),
    INVOKER_FAIL(21, "服务调用失败"),
    SERVER_CONNECT_FAIL(22, "服务器连接失败"),
    SERVER_NOT_FOUND(23, "服务未找到"),
    FUNCTION_LOCKED(24, "功能未解锁"),
    SERVER_CONNECT_TIMEOUT(25, "服务器连接超时"),
    SERVER_IS_BUSY(26, "服务器繁忙中"),
    CLUSTER_INVOKE_FAIL(27, "集群调用失败"),
    PARSE_PROTO_FAIL(28, "协议解析报错"),

    PARAM_ERROR(29, "参数错误"),;
    private final int code;
    @Setter
    private String msg;

    GameError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String err(long roleId, Object... params) {
        return String.format("roleId=%d, msg=%s", roleId, Objects.nonNull(params) ? Arrays.toString(params) : "[]");
    }

    public static GameError valueOf(int code) {
        for (GameError err : values()) {
            if (err.getCode() == code) {
                return err;
            }
        }

        return null;
    }

    /**
     * 错误码重复检查
     */
    public static void codeDuplicateCheck() {
        BitSet bitSet = new BitSet();
        for (GameError error : values()) {
            if (!bitSet.get(error.getCode())) {
                bitSet.set(error.getCode());
            } else {
                throw new IllegalStateException("error code duplicate: " + error);
            }
        }
    }

}
