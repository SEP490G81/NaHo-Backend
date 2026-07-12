package org.naho.point.exception;

import org.naho.i18n.message.point.PointHistoryTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum PointHistoryDomainErrorCode implements ErrorCode {
    POINT_HISTORY_USER_ID_NOT_VALID(
            "POINT_HISTORY_001",
            PointHistoryTitleMessageKey.POINT_HISTORY_USER_ID_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_POINT_NOT_VALID(
            "POINT_HISTORY_002",
            PointHistoryTitleMessageKey.POINT_HISTORY_POINT_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_TRANSACTION_TYPE_NOT_VALID(
            "POINT_HISTORY_003",
            PointHistoryTitleMessageKey.POINT_HISTORY_TRANSACTION_TYPE_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_TRANSACTION_TIME_NOT_VALID(
            "POINT_HISTORY_004",
            PointHistoryTitleMessageKey.POINT_HISTORY_TRANSACTION_TIME_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_LEARNING_PATH_NODE_ID_NOT_VALID(
            "POINT_HISTORY_005",
            PointHistoryTitleMessageKey.POINT_HISTORY_LEARNING_PATH_NODE_ID_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_OBJECTIVE_ID_NOT_VALID(
            "POINT_HISTORY_006",
            PointHistoryTitleMessageKey.POINT_HISTORY_OBJECTIVE_ID_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_LESSON_ID_NOT_VALID(
            "POINT_HISTORY_007",
            PointHistoryTitleMessageKey.POINT_HISTORY_LESSON_ID_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_TOPIC_ID_NOT_VALID(
            "POINT_HISTORY_008",
            PointHistoryTitleMessageKey.POINT_HISTORY_TOPIC_ID_NOT_VALID_TITLE,
            400
    ),
    POINT_HISTORY_BOOK_ID_NOT_VALID(
            "POINT_HISTORY_009",
            PointHistoryTitleMessageKey.POINT_HISTORY_BOOK_ID_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    PointHistoryDomainErrorCode(String code, String titleKey, int statusCode) {
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
