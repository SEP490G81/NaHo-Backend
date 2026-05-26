package org.naho.shared.exception;

public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;

    public DomainException(ErrorCode errorCode, String detailMessage, Object... args) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.args = args;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() {
        return args;
    }
}
