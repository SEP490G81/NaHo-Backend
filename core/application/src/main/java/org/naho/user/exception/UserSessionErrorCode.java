package org.naho.user.exception;

import org.naho.i18n.message.user.UserSessionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserSessionErrorCode implements ErrorCode {
    USER_SESSION_NOT_FOUND(
            "USER_SESSION_A001",
            UserSessionTitleMessageKey.USER_SESSION_NOT_FOUND_TITLE
    ),
    ;
    
    private final String code;
    private final String titleKey;

    UserSessionErrorCode(String code, String titleKey) {
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
