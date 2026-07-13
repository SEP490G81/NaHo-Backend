package org.naho.user.exception;

import org.naho.i18n.message.user.UserSessionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserSessionDomainErrorCode implements ErrorCode {
    USER_SESSION_DEVICE_ID_NOT_VALID(
            "USER_SESSION_001",
            UserSessionTitleMessageKey.USER_SESSION_DEVICE_ID_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserSessionDomainErrorCode(String code, String titleKey, int statusCode) {
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
