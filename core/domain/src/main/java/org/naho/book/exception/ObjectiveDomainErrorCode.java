package org.naho.book.exception;

import org.naho.i18n.message.book.ObjectiveTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum ObjectiveDomainErrorCode implements ErrorCode {
    OBJECTIVE_ORDER_INDEX_EMPTY("OBJECTIVE_001", ObjectiveTitleMessageKey.OBJECTIVE_CREATION_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    ObjectiveDomainErrorCode(String code, String titleKey, int statusCode) {
        this.code = code;
        this.titleKey = titleKey;
        this.statusCode = statusCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitleKey() {
        return titleKey;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
