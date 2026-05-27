package org.naho.user.exception;

import org.naho.shared.exception.ErrorCode;
import org.naho.user.constant.UserApplicationMessageKey;

public enum UserApplicationErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_A001", UserApplicationMessageKey.USER_NOT_FOUND_TITLE),
    USER_ALREADY_EXISTS("USER_A002", UserApplicationMessageKey.USER_ALREADY_EXISTS_TITLE),
    USER_ROLE_NOT_VALID("USER_A003", UserApplicationMessageKey.USER_ROLE_NOT_VALID_TITLE);

    private final String code;
    private final String titleKey;

    UserApplicationErrorCode(String code, String titleKey) {
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
