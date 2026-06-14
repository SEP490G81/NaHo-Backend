package org.naho.user.exception;

import org.naho.i18n.message.user.RoleTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum RoleErrorCode implements ErrorCode {
    ROLE_NOT_FOUND(
            "ROLE_A001",
            RoleTitleMessageKey.ROLE_NOT_FOUND_TITLE,
            404
    );
    private final String code;
    private final String titleKey;
    private final int statusCode;

    RoleErrorCode(String code, String titleKey, int statusCode) {
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
