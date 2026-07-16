package org.naho.persona.exception;

import org.naho.i18n.message.persona.PersonaTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum PersonaDomainErrorCode implements ErrorCode {
    PERSONA_NAME_NOT_VALID(
            "PERSONA_001",
            PersonaTitleMessageKey.PERSONA_NAME_NOT_VALID_TITLE,
            400
    ),
    PERSONA_PROMPT_NOT_VALID(
            "PERSONA_002",
            PersonaTitleMessageKey.PERSONA_PROMPT_NOT_VALID_TITLE,
            400
    ),
    PERSONA_NOT_FOUND(
            "PERSONA_003",
            PersonaTitleMessageKey.PERSONA_NOT_FOUND_TITLE,
            404
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    PersonaDomainErrorCode(String code, String titleKey, int statusCode) {
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
