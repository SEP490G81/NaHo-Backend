package org.naho.user.exception;

import org.naho.shared.exception.BaseException;
import org.naho.shared.exception.ErrorCode;

public class DomainException extends BaseException {
    public DomainException(ErrorCode errorCode, String detailMessage, Object... args) {
        super(errorCode, detailMessage, args);
    }
}
