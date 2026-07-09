package org.naho.point.exception;

import org.naho.i18n.message.point.PointSummaryTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum PointSummaryDomainErrorCode implements ErrorCode {
    POINT_SUMMARY_USER_ID_NOT_VALID(
            "POINT_SUMMARY_001",
            PointSummaryTitleMessageKey.POINT_SUMMARY_USER_ID_NOT_VALID_TITLE,
            400
    ),
    POINT_SUMMARY_TOTAL_POINT_NOT_VALID(
            "POINT_SUMMARY_002",
            PointSummaryTitleMessageKey.POINT_SUMMARY_TOTAL_POINT_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    PointSummaryDomainErrorCode(String code, String titleKey, int statusCode) {
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
