package org.naho.question.exception;

import org.naho.i18n.message.question.ChestTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum ChestErrorCode implements ErrorCode {
    CHEST_NOT_FOUND("CHEST_A001", ChestTitleMessageKey.CHEST_NOT_FOUND_TITLE, 404);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    ChestErrorCode(String code, String titleKey, int statusCode) {
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
