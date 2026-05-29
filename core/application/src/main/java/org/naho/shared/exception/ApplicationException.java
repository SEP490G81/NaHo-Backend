package org.naho.shared.exception;

public class ApplicationException extends BaseException {
    public ApplicationException(ErrorCode errorCode, String detailMessage, Object... args) {
        super(errorCode, detailMessage, args);
    }
}
