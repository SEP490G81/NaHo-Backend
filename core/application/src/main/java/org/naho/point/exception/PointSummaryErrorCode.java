package org.naho.point.exception;

import org.naho.i18n.message.point.PointSummaryTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum PointSummaryErrorCode implements ErrorCode {
    POINT_SUMMARY_NOT_FOUND("POINT_SUMMARY_A001", PointSummaryTitleMessageKey.POINT_SUMMARY_NOT_FOUND_TITLE, 404),
    POINT_SUMMARY_POINT_INVALID("POINT_SUMMARY_A002", PointSummaryTitleMessageKey.POINT_SUMMARY_TOTAL_POINT_NOT_VALID_TITLE, 400),
    POINT_SUMMARY_USER_ID_INVALID("POINT_SUMMARY_A003", PointSummaryTitleMessageKey.POINT_SUMMARY_USER_ID_NOT_VALID_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    PointSummaryErrorCode(String code, String titleKey, int statusCode) {
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
