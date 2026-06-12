package org.naho.user.exception;

import org.naho.i18n.message.user.RoleTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum RoleErrorCode implements ErrorCode {
    ROLE_NOT_FOUND(
            "ROLE_A001",
            RoleTitleMessageKey.ROLE_NOT_FOUND_TITLE
    );
    private final String code;
    private final String titleKey;

    RoleErrorCode(String code, String titleKey) {
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
