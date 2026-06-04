package org.naho.speech.llm.exception;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.llm.LlmTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum LlmApplicationError implements ErrorCode {

    LLM_TOPIC_INVALID(
            "LLM_A001",
            LlmTitleMessageKey.LLM_TOPIC_INVALID_TITLE
    ),
    LLM_SESSION_ID_INVALID(
            "LLM_A002",
            LlmTitleMessageKey.LLM_SESSION_ID_INVALID_TITLE
    ),
    LLM_USER_MESSAGE_INVALID(
            "LLM_A003",
            LlmTitleMessageKey.LLM_USER_MESSAGE_INVALID_TITLE
    ),
    LLM_API_ERROR(
            "LLM_I001",
            LlmDetailMessageKey.LLM_API_ERROR
    ),
    LLM_STREAMING_ERROR(
            "LLM_I002",
            LlmDetailMessageKey.LLM_STREAMING_ERROR
    ),
    LLM_PARSE_ERROR(
            "LLM_I003",
            LlmDetailMessageKey.LLM_PARSE_ERROR
    ),
    LLM_CONNECTION_TIMEOUT(
            "LLM_I004",
            LlmDetailMessageKey.LLM_CONNECTION_TIMEOUT
    );
    final private String code;
    final private String titleKey;

    LlmApplicationError(String code, String titleKey) {
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
