package org.naho.user.exception;

import org.naho.shared.exception.ErrorCode;
import org.naho.user.constant.UserMessageKey;

public enum UserErrorCode implements ErrorCode {

    USER_AGE_NOT_VALID(
            "USER_001",
            UserMessageKey.USER_AGE_NOT_VALID_TITLE
    ),

    USER_EMAIL_NOT_VALID(
            "USER_002",
            UserMessageKey.USER_EMAIL_NOT_VALID_TITLE
    ),

    USER_USERNAME_NOT_VALID(
            "USER_003",
            UserMessageKey.USER_USERNAME_NOT_VALID_TITLE
    ),

    USER_TIMEZONE_NOT_VALID(
            "USER_OO4",
            UserMessageKey.USER_TIMEZONE_NOT_VALID_TITLE
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