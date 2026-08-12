package org.naho.speech.llm.exception;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.llm.LlmTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum LlmApplicationError implements ErrorCode {

    LLM_TOPIC_INVALID(
            "LLM_A001",
            LlmTitleMessageKey.LLM_TOPIC_INVALID_TITLE,
            400),
    LLM_SESSION_ID_INVALID(
            "LLM_A002",
            LlmTitleMessageKey.LLM_SESSION_ID_INVALID_TITLE,
            400),
    LLM_USER_MESSAGE_INVALID(
            "LLM_A003",
            LlmTitleMessageKey.LLM_USER_MESSAGE_INVALID_TITLE,
            400),
    LLM_SESSION_ALREADY_COMPLETED(
            "LLM_A004",
            LlmTitleMessageKey.LLM_SESSION_ALREADY_COMPLETED_TITLE,
            400),
    LLM_SESSION_NOT_FOUND(
            "LLM_A005",
            LlmTitleMessageKey.LLM_SESSION_NOT_FOUND_TITLE,
            404),
    LLM_SESSION_TURN_LIMIT_EXCEEDED(
            "LLM_A006",
            LlmTitleMessageKey.LLM_SESSION_TURN_LIMIT_EXCEEDED_TITLE,
            400),
    LLM_API_ERROR(
            "LLM_I001",
            LlmDetailMessageKey.LLM_API_ERROR,
            502),
    LLM_STREAMING_ERROR(
            "LLM_I002",
            LlmDetailMessageKey.LLM_STREAMING_ERROR,
            500),
    LLM_PARSE_ERROR(
            "LLM_I003",
            LlmDetailMessageKey.LLM_PARSE_ERROR,
            500),
    LLM_CONNECTION_TIMEOUT(
            "LLM_I004",
            LlmDetailMessageKey.LLM_CONNECTION_TIMEOUT,
            504);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    LlmApplicationError(String code, String titleKey, int statusCode) {
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
