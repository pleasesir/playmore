package org.playmore.api.exception;


import org.playmore.common.exception.ExceptionMessage;
import org.playmore.common.exception.TreasureException;

import java.io.Serial;

/**
 * @author zhangpeng
 * @version 1.0
 * @date 2025-04-26 23:04
 * @description TODO
 */
public class MwException extends TreasureException {
    @Serial
    private static final long serialVersionUID = 6895372785988358197L;

    public MwException() {
        super();
    }

    public MwException(String message) {
        super(message);
    }

    public MwException(int code, Object... message) {
        super(ExceptionMessage.spliceMessage(code, message));
        this.code = code;
    }

    public MwException(GameError gameError, Object... message) {
        super(ExceptionMessage.spliceMessage(gameError, message));
        code = gameError.getCode();
    }

    public MwException(String message, Throwable t) {
        super(message, t);
    }

    @Override
    public String toString() {
        return "MwException [code=" + code + ", message=" + getMessage() + "]";
    }
}
