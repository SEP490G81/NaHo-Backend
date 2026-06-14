package org.naho.user.exception;

import org.naho.i18n.message.user.UserSessionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserSessionErrorCode implements ErrorCode {
    USER_SESSION_NOT_FOUND(
            "USER_SESSION_A001",
            UserSessionTitleMessageKey.USER_SESSION_NOT_FOUND_TITLE,
            404
    );
    
    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserSessionErrorCode(String code, String titleKey, int statusCode) {
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
