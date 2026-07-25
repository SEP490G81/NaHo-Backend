package org.naho.daily.exception;

import org.naho.i18n.message.daily.UserDailyAttendanceTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserDailyAttendanceDomainErrorCode implements ErrorCode {
    USER_DAILY_ATTENDANCE_USER_ID_REQUIRED(
            "USER_DAILY_ATTENDANCE_001",
            UserDailyAttendanceTitleMessageKey.USER_DAILY_ATTENDANCE_FAILED_TITLE,
            400
    ),
    USER_DAILY_ATTENDANCE_DAILY_REWARD_ID_REQUIRED(
            "USER_DAILY_ATTENDANCE_002",
            UserDailyAttendanceTitleMessageKey.USER_DAILY_ATTENDANCE_FAILED_TITLE,
            400
    ),
    USER_DAILY_ATTENDANCE_DATE_REQUIRED(
            "USER_DAILY_ATTENDANCE_003",
            UserDailyAttendanceTitleMessageKey.USER_DAILY_ATTENDANCE_FAILED_TITLE,
            400
    ),
    USER_DAILY_ATTENDANCE_DATE_FUTURE(
            "USER_DAILY_ATTENDANCE_004",
            UserDailyAttendanceTitleMessageKey.USER_DAILY_ATTENDANCE_FAILED_TITLE,
            400
    ),
    USER_DAILY_ATTENDANCE_ALREADY_EXISTS(
            "USER_DAILY_ATTENDANCE_005",
            UserDailyAttendanceTitleMessageKey.USER_DAILY_ATTENDANCE_FAILED_TITLE,
            400
    ),
    USER_DAILY_ATTENDANCE_NOT_FOUND(
            "USER_DAILY_ATTENDANCE_006",
            UserDailyAttendanceTitleMessageKey.USER_DAILY_ATTENDANCE_NOT_FOUND_TITLE,
            404
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserDailyAttendanceDomainErrorCode(String code, String titleKey, int statusCode) {
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
