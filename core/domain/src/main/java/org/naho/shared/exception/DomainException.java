package org.naho.shared.exception;

public class DomainException extends BaseException {

    public DomainException(ErrorCode errorCode, String detailMessage, Object... args) {
        super(errorCode, detailMessage, args);
    }
}
