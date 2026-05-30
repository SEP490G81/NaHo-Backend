package org.naho.user.exception;

import org.naho.shared.exception.ErrorCode;
import org.naho.user.constant.UserApplicationMessageKey;

public enum UserApplicationErrorCode implements ErrorCode {
    USER_UNAUTHORIZED("USER_A001", UserApplicationMessageKey.USER_UNAUTHORIZED_TITLE),
    USER_ACCESS_DENIED("USER_A002", UserApplicationMessageKey.USER_ACCESS_DENIED_TITLE),
    USER_NOT_FOUND("USER_A003", UserApplicationMessageKey.USER_NOT_FOUND_TITLE),
    USER_LOGIN_FAILED("USER_A004", UserApplicationMessageKey.USER_LOGIN_FAILED_TITLE),
    USER_HASH_FAILED("USER_A005", UserApplicationMessageKey.USER_HASH_FAILED_TITLE),
    USER_ALREADY_EXISTS("USER_A006", UserApplicationMessageKey.USER_ALREADY_EXISTS_TITLE),
    USER_ROLE_NOT_VALID("USER_A007", UserApplicationMessageKey.USER_ROLE_NOT_VALID_TITLE),
    USER_LIST_GET_FAILED("USER_A006", UserApplicationMessageKey.USER_LIST_GET_FAILED),
    USER_UPDATE_FAILED("USER_A007", UserApplicationMessageKey.USER_UPDATE_STATUS_FAILED);

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
