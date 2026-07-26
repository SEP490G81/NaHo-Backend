package org.naho.daily.exception;

import org.naho.i18n.message.daily.DailyMissionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum DailyMissionDomainErrorCode implements ErrorCode {
    DAILY_MISSION_CHEST_ID_REQUIRED(
            "DAILY_MISSION_001",
            DailyMissionTitleMessageKey.DAILY_MISSION_CREATION_FAILED_TITLE,
            400
    ),
    DAILY_MISSION_TITLE_REQUIRED(
            "DAILY_MISSION_002",
            DailyMissionTitleMessageKey.DAILY_MISSION_CREATION_FAILED_TITLE,
            400
    ),
    DAILY_MISSION_DATE_REQUIRED(
            "DAILY_MISSION_003",
            DailyMissionTitleMessageKey.DAILY_MISSION_CREATION_FAILED_TITLE,
            400
    ),
    DAILY_MISSION_TYPE_REQUIRED(
            "DAILY_MISSION_004",
            DailyMissionTitleMessageKey.DAILY_MISSION_CREATION_FAILED_TITLE,
            400
    ),
    DAILY_MISSION_NOT_FOUND(
            "DAILY_MISSION_005",
            DailyMissionTitleMessageKey.DAILY_MISSION_NOT_FOUND_TITLE,
            404
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    DailyMissionDomainErrorCode(String code, String titleKey, int statusCode) {
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
