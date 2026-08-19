package org.naho.book.exception;

import org.naho.i18n.message.book.ObjectiveTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum ObjectiveErrorCode implements ErrorCode {
    OBJECTIVE_NOT_FOUND("OBJECTIVE_A001", ObjectiveTitleMessageKey.OBJECTIVE_GET_FAIL_TITLE, 404),
    OBJECTIVE_UPDATE_FORBIDDEN("OBJECTIVE_A003", ObjectiveTitleMessageKey.OBJECTIVE_UPDATE_FAILED_TITLE, 403);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    ObjectiveErrorCode(String code, String titleKey, int statusCode) {
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
