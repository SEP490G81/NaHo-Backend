package org.naho.point.exception;

import org.naho.i18n.message.point.SeasonTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum SeasonDomainErrorCode implements ErrorCode {
    SEASON_NO_EMPTY(
            "SEASON_001",
            SeasonTitleMessageKey.SEASON_CREATION_FAILED_TITLE,
            400
    ),
    SEASON_START_AT_EMPTY(
            "SEASON_002",
            SeasonTitleMessageKey.SEASON_CREATION_FAILED_TITLE,
            400
    ),
    SEASON_END_AT_EMPTY(
            "SEASON_003",
            SeasonTitleMessageKey.SEASON_CREATION_FAILED_TITLE,
            400
    ),
    SEASON_END_AT_BEFORE_OR_EQUAL_TO_START_AT(
            "SEASON_004",
            SeasonTitleMessageKey.SEASON_CREATION_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    SeasonDomainErrorCode(String code, String titleKey, int statusCode) {
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
