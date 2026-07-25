package org.naho.social.exception;

import org.naho.i18n.message.social.ReportTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum ReportDomainErrorCode implements ErrorCode {
    REPORT_USER_ID_NOT_VALID(
            "REPORT_001",
            ReportTitleMessageKey.REPORT_USER_ID_NOT_VALID_TITLE,
            400
    ),
    REPORT_TYPE_NOT_VALID(
            "REPORT_002",
            ReportTitleMessageKey.REPORT_TYPE_NOT_VALID_TITLE,
            400
    ),
    REPORT_TITLE_NOT_VALID(
            "REPORT_003",
            ReportTitleMessageKey.REPORT_TITLE_NOT_VALID_TITLE,
            400
    ),
    REPORT_DESCRIPTION_NOT_VALID(
            "REPORT_004",
            ReportTitleMessageKey.REPORT_DESCRIPTION_NOT_VALID_TITLE,
            400
    ),
    REPORT_QUESTION_ID_NOT_VALID(
            "REPORT_005",
            ReportTitleMessageKey.REPORT_QUESTION_ID_NOT_VALID_TITLE,
            400
    ),
    REPORT_COMMENT_ID_NOT_VALID(
            "REPORT_006",
            ReportTitleMessageKey.REPORT_COMMENT_ID_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    ReportDomainErrorCode(String code, String titleKey, int statusCode) {
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
