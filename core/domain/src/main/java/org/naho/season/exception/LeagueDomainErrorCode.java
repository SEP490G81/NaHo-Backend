package org.naho.season.exception;

import org.naho.i18n.message.season.LeagueTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum LeagueDomainErrorCode implements ErrorCode {
    LEAGUE_ICON_FILE_EMPTY(
            "LEAGUE_001",
            LeagueTitleMessageKey.LEAGUE_CREATION_FAILED_TITLE,
            400
    ),
    LEAGUE_NAME_EMPTY(
            "LEAGUE_002",
            LeagueTitleMessageKey.LEAGUE_CREATION_FAILED_TITLE,
            400
    ),
    LEAGUE_MIN_POINT_EMPTY(
            "LEAGUE_003",
            LeagueTitleMessageKey.LEAGUE_CREATION_FAILED_TITLE,
            400
    ),
    LEAGUE_MAX_POINT_EMPTY(
            "LEAGUE_004",
            LeagueTitleMessageKey.LEAGUE_CREATION_FAILED_TITLE,
            400
    ),
    LEAGUE_MIN_POINT_GREATER_THAN_OR_EQUAL_TO_MAX_POINT(
            "LEAGUE_005",
            LeagueTitleMessageKey.LEAGUE_CREATION_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    LeagueDomainErrorCode(String code, String titleKey, int statusCode) {
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
