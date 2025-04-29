package org.playmore.common.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class TreasureException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2672414210409676158L;

    /**
     * 错误码
     */
    protected int code;

    public TreasureException(Throwable cause) {
        super(cause);
    }

    protected TreasureException() {
    }

    protected TreasureException(String message) {
        super(message);
    }

    public TreasureException(int code, Object... message) {
        super(ExceptionMessage.spliceMessage(message));
        this.code = code;
    }

    public TreasureException(String message, Throwable cause) {
        super(message, cause);
    }

    public TreasureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
