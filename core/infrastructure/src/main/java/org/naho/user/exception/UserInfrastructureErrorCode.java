package org.naho.user.exception;

import org.naho.shared.exception.ErrorCode;
import org.naho.user.constant.UserInfrastructureMessageKey;

public enum UserInfrastructureErrorCode implements ErrorCode {
    USER_DB_ERROR("USER_I001", UserInfrastructureMessageKey.USER_DB_ERROR),
    JWT_TOKEN_CREATION_FAILED("USER_I002", UserInfrastructureMessageKey.JWT_TOKEN_CREATION_FAILED),
    PASSWORD_ENCODING_FAILED("USER_I003", UserInfrastructureMessageKey.PASSWORD_ENCODING_FAILED);

    private final String code;
    private final String titleKey;

    UserInfrastructureErrorCode(String code, String titleKey) {
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
