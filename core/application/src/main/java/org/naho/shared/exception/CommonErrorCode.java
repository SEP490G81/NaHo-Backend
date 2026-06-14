package org.naho.shared.exception;

public enum CommonErrorCode implements ErrorCode {

    COMMON_INTERNAL_SERVER_ERROR(
            "COMMON_A001",
            org.naho.i18n.message.common.CommonTitleMessageKey.COMMON_INTERNAL_SERVER_ERROR_TITLE,
            500
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
