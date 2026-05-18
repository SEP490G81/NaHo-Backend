package org.naho.user.exception;

import org.naho.shared.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    AGE_NOT_VALID("USER_001", "user.age.not.valid"),
    EMAIL_REQUIRED("USER_002", "user.email.required"),
    EMAIL_NOT_VALID("USER_003", "user.email.not.valid"),
    PASSWORD_REQUIRED("USER_004", "user.password.required"),
    PASSWORD_NOT_VALID("USER_005", "user.password.not.valid"),
    USERNAME_REQUIRED("USER_006", "user.username.required"),
    USERNAME_NOT_VALID("USER_007", "user.username.not.valid"),
    ;

    private final String code;
    private final String title;

    UserErrorCode(String code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
