package org.naho.ai.exception;

import org.naho.ai.constant.AiInfrastructureMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum AiInfrastructureErrorCode implements ErrorCode {
    OPENAI_API_ERROR("AI_I001", AiInfrastructureMessageKey.OPENAI_API_ERROR),
    OPENAI_STREAMING_ERROR("AI_I002", AiInfrastructureMessageKey.OPENAI_STREAMING_ERROR),
    OPENAI_PARSE_ERROR("AI_I003", AiInfrastructureMessageKey.OPENAI_PARSE_ERROR),
    OPENAI_CONNECTION_TIMEOUT("AI_I004", AiInfrastructureMessageKey.OPENAI_CONNECTION_TIMEOUT);

    private final String code;
    private final String titleKey;

    AiInfrastructureErrorCode(String code, String titleKey) {
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
