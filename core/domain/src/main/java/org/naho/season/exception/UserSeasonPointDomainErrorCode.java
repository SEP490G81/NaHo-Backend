package org.naho.season.exception;

import org.naho.i18n.message.season.UserSeasonPointTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserSeasonPointDomainErrorCode implements ErrorCode {
    USER_SEASON_POINT_USER_EMPTY(
            "USER_SEASON_POINT_001",
            UserSeasonPointTitleMessageKey.USER_SEASON_POINT_CREATION_FAILED_TITLE,
            400
    ),
    USER_SEASON_POINT_SEASON_EMPTY(
            "USER_SEASON_POINT_002",
            UserSeasonPointTitleMessageKey.USER_SEASON_POINT_CREATION_FAILED_TITLE,
            400
    ),
    USER_SEASON_POINT_LEAGUE_EMPTY(
            "USER_SEASON_POINT_003",
            UserSeasonPointTitleMessageKey.USER_SEASON_POINT_CREATION_FAILED_TITLE,
            400
    ),
    USER_SEASON_POINT_SEASON_POINT_EMPTY(
            "USER_SEASON_POINT_004",
            UserSeasonPointTitleMessageKey.USER_SEASON_POINT_CREATION_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserSeasonPointDomainErrorCode(String code, String titleKey, int statusCode) {
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
