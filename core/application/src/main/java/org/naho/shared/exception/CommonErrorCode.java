package org.naho.shared.exception;

public enum CommonErrorCode implements ErrorCode {

    COMMON_INTERNAL_SERVER_ERROR(
            "COMMON_A001",
            org.naho.i18n.message.common.CommonTitleMessageKey.COMMON_INTERNAL_SERVER_ERROR_TITLE
    );

    private final String code;
    private final String titleKey;

    CommonErrorCode(String code, String titleKey) {
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
