package org.naho.speech.topic.exception;

import org.naho.i18n.message.speech.TopicTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum TopicErrorCode implements ErrorCode {
    TOPIC_NOT_FOUND("TOPIC_001", TopicTitleMessageKey.TOPIC_NOT_FOUND_TITLE),
    TOPIC_CREATION_FAILED("TOPIC_002", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE),
    TOPIC_NAME_EMPTY("TOPIC_003", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE),
    TOPIC_DESCRIPTION_EMPTY("TOPIC_004", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE),
    TOPIC_ALREADY_EXISTS_IN_LEVEL("TOPIC_005", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE),
    TOPIC_ORDER_INDEX_INVALID("TOPIC_006", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE);

    private final String code;
    private final String titleKey;

    TopicErrorCode(String code, String titleKey) {
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
