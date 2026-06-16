package org.naho.user.exception;

import org.naho.i18n.message.user.UserTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserDomainErrorCode implements ErrorCode {

    USER_AGE_NOT_VALID(
            "USER_001",
            UserTitleMessageKey.USER_AGE_NOT_VALID_TITLE,
            400
    ),

    USER_EMAIL_NOT_VALID(
            "USER_002",
            UserTitleMessageKey.USER_EMAIL_NOT_VALID_TITLE,
            400
    ),

    USER_USERNAME_NOT_VALID(
            "USER_003",
            UserTitleMessageKey.USER_USERNAME_NOT_VALID_TITLE,
            400
    ),

    USER_TIMEZONE_NOT_VALID(
            "USER_OO4",
            UserTitleMessageKey.USER_TIMEZONE_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserDomainErrorCode(String code, String titleKey, int statusCode) {
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