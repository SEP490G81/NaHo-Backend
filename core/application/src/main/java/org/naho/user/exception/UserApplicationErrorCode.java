package org.naho.user.exception;

import org.naho.shared.exception.ErrorCode;
import org.naho.user.constant.UserApplicationMessageKey;

public enum UserApplicationErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_A001", UserApplicationMessageKey.USER_NOT_FOUND_TITLE);

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
