package org.naho.speech.exception;

import org.naho.i18n.message.speech.TopicTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum TopicDomainErrorCode implements ErrorCode {
    TOPIC_NAME_EMPTY("TOPIC_001", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE),
    TOPIC_DESCRIPTION_EMPTY("TOPIC_002", TopicTitleMessageKey.TOPIC_CREATION_FAILED_TITLE);

    private final String code;
    private final String titleKey;

    TopicDomainErrorCode(String code, String titleKey) {
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
