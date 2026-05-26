package org.naho.user.exception;

import org.naho.shared.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {

    USER_AGE_NOT_VALID(
            "USER_001",
            UserErrorKey.USER_AGE_NOT_VALID_TITLE
    ),

    USER_EMAIL_NOT_VALID(
            "USER_002",
            UserErrorKey.USER_EMAIL_NOT_VALID_TITLE
    ),

    USER_USERNAME_NOT_VALID(
            "USER_003",
            UserErrorKey.USER_USERNAME_NOT_VALID_TITLE
    ),

    USER_TIMEZONE_NOT_VALID(
            "USER_OO4",
            UserErrorKey.USER_TIMEZONE_NOT_VALID_TITLE
    );

    private final String code;
    private final String titleKey;

    UserErrorCode(String code, String titleKey) {
        this.code = code;
        this.titleKey = titleKey;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitleKey() {
        return titleKey;
    }
}