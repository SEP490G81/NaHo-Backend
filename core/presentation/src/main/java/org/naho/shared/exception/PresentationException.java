package org.naho.shared.exception;

public class PresentationException extends BaseException {
    public PresentationException(ErrorCode errorCode, String detailMessage, Object... args) {
        super(errorCode, detailMessage, args);
    }
}
