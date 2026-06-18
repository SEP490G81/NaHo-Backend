package org.naho.topic.exception;

import org.naho.i18n.message.topic.TopicTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum TopicDomainErrorCode implements ErrorCode {
    TOPIC_NAME_EMPTY("TOPIC_001", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE, 400),
    TOPIC_DESCRIPTION_EMPTY("TOPIC_002", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    TopicDomainErrorCode(String code, String titleKey, int statusCode) {
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
