package org.naho.shared.exception;

import org.naho.i18n.message.common.CommonTitleMessageKey;

public enum CommonErrorCode implements ErrorCode {

    COMMON_INTERNAL_SERVER_ERROR(
            "COMMON_A001",
            CommonTitleMessageKey.COMMON_INTERNAL_SERVER_ERROR_TITLE,
            500
    ),
    COMMON_CANNOT_READ_JSON(
            "COMMON_A002",
            CommonTitleMessageKey.COMMON_CANNOT_READ_JSON_TITLE,
            500
    ),
    COMMON_INVALID_REQUEST(
            "COMMON_A003",
            CommonTitleMessageKey.COMMON_INVALID_REQUEST_TITLE,
            400
    ),
    COMMON_RESOURCE_NOT_FOUND(
            "COMMON_A004",
            CommonTitleMessageKey.COMMON_RESOURCE_NOT_FOUND_TITLE,
            404
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    CommonErrorCode(String code, String titleKey, int statusCode) {
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
