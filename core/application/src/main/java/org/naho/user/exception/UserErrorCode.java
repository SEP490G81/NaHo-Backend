package org.naho.user.exception;

import org.naho.i18n.message.user.UserTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    USER_UNAUTHORIZED(
            "USER_A001",
            UserTitleMessageKey.USER_UNAUTHORIZED_TITLE,
            401),
    USER_ACCESS_DENIED(
            "USER_A002",
            UserTitleMessageKey.USER_ACCESS_DENIED_TITLE,
            403),
    USER_NOT_FOUND(
            "USER_A003",
            UserTitleMessageKey.USER_NOT_FOUND_TITLE,
            404),
    USER_LOGIN_FAILED(
            "USER_A004",
            UserTitleMessageKey.USER_LOGIN_FAILED_TITLE,
            400),
    USER_HASH_FAILED(
            "USER_A005",
            UserTitleMessageKey.USER_HASH_FAILED_TITLE,
            500),
    USER_ALREADY_EXISTS(
            "USER_A006",
            UserTitleMessageKey.USER_ALREADY_EXISTS_TITLE,
            409),
    USER_ROLE_NOT_VALID(
            "USER_A007",
            UserTitleMessageKey.USER_ROLE_NOT_VALID_TITLE,
            400),
    USER_PERSIST_FAILED(
            "USER_A008",
            UserTitleMessageKey.USER_PERSIST_FAILED_TITLE,
            500),
    USER_INVALID_REFRESH_TOKEN(
            "USER_A009",
            UserTitleMessageKey.USER_INVALID_REFRESH_TOKEN_TITLE,
            401),
    USER_GOOGLE_ID_TOKEN_NOT_VALID(
            "USER_A010",
            UserTitleMessageKey.USER_GOOGLE_ID_TOKEN_NOT_VALID_TITLE,
            400),
    USER_EMAIL_UNVERIFIED(
            "USER_A011",
            UserTitleMessageKey.USER_EMAIL_UNVERIFIED_TITLE,
            403),
    USER_INVALID_OTP(
            "USER_A012",
            UserTitleMessageKey.USER_INVALID_OTP_TITLE,
            400),
    USER_OTP_COOLDOWN(
            "USER_A013",
            UserTitleMessageKey.USER_OTP_COOLDOWN_TITLE,
            429),
    USER_OTP_ATTEMPTS_EXCEEDED(
            "USER_A014",
            UserTitleMessageKey.USER_OTP_ATTEMPTS_EXCEEDED_TITLE,
            400),
    USER_PASSWORD_NOT_MATCH(
            "USER_A015",
            UserTitleMessageKey.USER_PASSWORD_NOT_MATCH_TITLE,
            400),
    USER_PASSWORD_SAME_AS_OLD(
            "USER_A017",
            UserTitleMessageKey.USER_PASSWORD_SAME_AS_OLD_TITLE,
            400),
    USER_OLD_PASSWORD_NOT_MATCH(
            "USER_A018",
            UserTitleMessageKey.USER_OLD_PASSWORD_NOT_MATCH_TITLE,
            400),
    USER_INVALID_RESET_TOKEN(
            "USER_A019",
            UserTitleMessageKey.USER_INVALID_RESET_TOKEN_TITLE,
            401),
    USER_SOCIAL_LOGIN_CANNOT_RESET_PASSWORD(
            "USER_A020",
            UserTitleMessageKey.USER_SOCIAL_LOGIN_CANNOT_RESET_PASSWORD_TITLE,
            400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserErrorCode(String code, String titleKey, int statusCode) {
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
