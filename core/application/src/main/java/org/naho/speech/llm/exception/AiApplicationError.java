package org.naho.speech.llm.exception;

import org.naho.speech.llm.constant.AiApplicationMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum AiApplicationError implements ErrorCode {

    AI_TOPIC_INVALID("AI_A001", AiApplicationMessageKey.AI_TOPIC_INVALID_TITLE),
    AI_SESSION_ID_INVALID("AI_A002", AiApplicationMessageKey.AI_SESSION_ID_INVALID_TITLE),
    AI_USER_MESSAGE_INVALID("AI_A003", AiApplicationMessageKey.AI_USER_MESSAGE_INVALID_TITLE);
    final private String code;
    final private String titleKey;

    AiApplicationError(String code, String titleKey) {
        this.code = code;
        this.titleKey = titleKey;
    }


    @Override
    public String getCode() {
        return "";
    }

    @Override
    public String getTitleKey() {
        return "";
    }
}
