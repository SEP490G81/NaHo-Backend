package org.naho.topic.exception;

import org.naho.i18n.message.topic.TopicTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum TopicErrorCode implements ErrorCode {
    TOPIC_NOT_FOUND("TOPIC_A001", TopicTitleMessageKey.TOPIC_GET_FAIL_TITLE, 404),
    TOPIC_CREATION_FAILED("TOPIC_A002", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE, 500),
    TOPIC_ALREADY_EXISTS("TOPIC_A003", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE, 409),
    TOPIC_ORDER_INDEX_INVALID("TOPIC_A004", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE, 400),
    TOPIC_UPDATE_FORBIDDEN("TOPIC_A005", TopicTitleMessageKey.TOPIC_UPDATE_FAILED_TITLE, 403),
    TOPIC_DELETE_FORBIDDEN("TOPIC_A006", TopicTitleMessageKey.TOPIC_DELETE_FAILED_TITLE, 403),
    OBJECTIVE_NOT_FOUND("TOPIC_A007", TopicTitleMessageKey.TOPIC_GET_FAIL_TITLE, 404);
    private final String code;
    private final String titleKey;
    private final int statusCode;

    TopicErrorCode(String code, String titleKey, int statusCode) {
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
