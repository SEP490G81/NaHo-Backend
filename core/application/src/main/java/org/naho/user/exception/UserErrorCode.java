package org.naho.user.exception;

import org.naho.i18n.message.user.UserTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    USER_UNAUTHORIZED(
            "USER_A001",
            UserTitleMessageKey.USER_UNAUTHORIZED_TITLE
    ),
    USER_ACCESS_DENIED(
            "USER_A002",
            UserTitleMessageKey.USER_ACCESS_DENIED_TITLE
    ),
    USER_NOT_FOUND(
            "USER_A003",
            UserTitleMessageKey.USER_NOT_FOUND_TITLE
    ),
    USER_LOGIN_FAILED(
            "USER_A004",
            UserTitleMessageKey.USER_LOGIN_FAILED_TITLE
    ),
    USER_HASH_FAILED(
            "USER_A005",
            UserTitleMessageKey.USER_HASH_FAILED_TITLE
    ),
    USER_ALREADY_EXISTS(
            "USER_A006",
            UserTitleMessageKey.USER_ALREADY_EXISTS_TITLE
    ),
    USER_ROLE_NOT_VALID(
            "USER_A007",
            UserTitleMessageKey.USER_ROLE_NOT_VALID_TITLE
    ),
    USER_PERSIST_FAILED(
            "USER_A008",
            UserTitleMessageKey.USER_PERSIST_FAILED_TITLE
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
