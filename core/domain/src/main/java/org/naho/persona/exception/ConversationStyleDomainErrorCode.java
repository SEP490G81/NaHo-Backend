package org.naho.persona.exception;

import org.naho.i18n.message.persona.ConversationStyleTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum ConversationStyleDomainErrorCode implements ErrorCode {
    CONVERSATION_STYLE_PROMPT_NOT_VALID(
            "CONV_STYLE_001",
            ConversationStyleTitleMessageKey.CONVERSATION_STYLE_PROMPT_NOT_VALID_TITLE,
            400
    ),
    CONVERSATION_STYLE_FORMALITY_LEVEL_NOT_VALID(
            "CONV_STYLE_002",
            ConversationStyleTitleMessageKey.CONVERSATION_STYLE_FORMALITY_LEVEL_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    ConversationStyleDomainErrorCode(String code, String titleKey, int statusCode) {
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
