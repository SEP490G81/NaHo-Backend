package org.naho.shared.exception;

public class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;

    public ApplicationException(ErrorCode errorCode, String detailMessage, Object... args) {
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
