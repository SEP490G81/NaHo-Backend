package org.naho.daily.exception;

import org.naho.i18n.message.daily.UserDailyMissionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserDailyMissionDomainErrorCode implements ErrorCode {
    USER_DAILY_MISSION_USER_ID_REQUIRED(
            "USER_DAILY_MISSION_001",
            UserDailyMissionTitleMessageKey.USER_DAILY_MISSION_FAILED_TITLE,
            400
    ),
    USER_DAILY_MISSION_DAILY_MISSION_ID_REQUIRED(
            "USER_DAILY_MISSION_002",
            UserDailyMissionTitleMessageKey.USER_DAILY_MISSION_FAILED_TITLE,
            400
    ),
    USER_DAILY_MISSION_COMPLETED_AT_REQUIRED(
            "USER_DAILY_MISSION_003",
            UserDailyMissionTitleMessageKey.USER_DAILY_MISSION_FAILED_TITLE,
            400
    ),
    USER_DAILY_MISSION_NOT_FOUND(
            "USER_DAILY_MISSION_004",
            UserDailyMissionTitleMessageKey.USER_DAILY_MISSION_NOT_FOUND_TITLE,
            404
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserDailyMissionDomainErrorCode(String code, String titleKey, int statusCode) {
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
