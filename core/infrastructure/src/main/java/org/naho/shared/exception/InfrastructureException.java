package org.naho.shared.exception;

public class InfrastructureException extends BaseException {
    public InfrastructureException(ErrorCode errorCode, String detailMessage, Object... args) {
        super(errorCode, detailMessage, args);
    }
}
