package org.naho.shared.exception;

public class BootstrapException extends BaseException {
    public BootstrapException(ErrorCode errorCode, String detailMessage, Object... args) {
        super(errorCode, detailMessage, args);
    }
}

