package org.naho.social.report.exception;

import org.naho.i18n.message.social.ReportTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum ReportErrorCode implements ErrorCode {
    REPORT_NOT_FOUND(
            "REPORT_A001",
            ReportTitleMessageKey.REPORT_GET_FAIL_TITLE,
            404);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    ReportErrorCode(String code, String titleKey, int statusCode) {
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
