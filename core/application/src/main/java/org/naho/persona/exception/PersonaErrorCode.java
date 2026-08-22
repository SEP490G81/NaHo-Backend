package org.naho.persona.exception;

import org.naho.i18n.message.persona.PersonaTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum PersonaErrorCode implements ErrorCode {
    PERSONA_NOT_FOUND(
            "PERSONA_E001",
            PersonaTitleMessageKey.PERSONA_NOT_FOUND_TITLE,
            404
    ),
    PERSONA_PERSIST_FAILED(
            "PERSONA_E002",
            PersonaTitleMessageKey.PERSONA_UPDATE_STATUS_FAILED_TITLE,
            500
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    PersonaErrorCode(String code, String titleKey, int statusCode) {
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
