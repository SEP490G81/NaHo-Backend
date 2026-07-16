package org.naho.season.exception;

import org.naho.i18n.message.season.SeasonTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum SeasonErrorCode implements ErrorCode {
    SEASON_OVERLAPPING("SEASON_A001", SeasonTitleMessageKey.SEASON_CREATION_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    SeasonErrorCode(String code, String titleKey, int statusCode) {
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
