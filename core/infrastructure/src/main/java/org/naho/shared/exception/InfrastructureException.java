package org.naho.shared.exception;

public class InfrastructureException extends RuntimeException {
    private ErrorCode errorCode;

    public InfrastructureException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
