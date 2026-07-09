package org.naho.point.exception;

import org.naho.i18n.message.point.PointHistoryTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum PointHistoryErrorCode implements ErrorCode {
    POINT_HISTORY_PAGE_INVALID(
            "POINT_HISTORY_A001",
            PointHistoryTitleMessageKey.POINT_HISTORY_PAGE_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_SIZE_INVALID(
            "POINT_HISTORY_A002",
            PointHistoryTitleMessageKey.POINT_HISTORY_SIZE_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_TRANSACTION_TIME_RANGE_INVALID(
            "POINT_HISTORY_A003",
            PointHistoryTitleMessageKey.POINT_HISTORY_TRANSACTION_TIME_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    PointHistoryErrorCode(String code, String titleKey, int statusCode) {
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
