package org.naho.grammar.exception;

import org.naho.shared.exception.ErrorCode;

public enum GrammarErrorCode implements ErrorCode {

    GRAMMAR_IMPORT_EMPTY(
            "GRAMMAR_E001",
            "grammar.import.failed.title",
            400
    ),
    GRAMMAR_IMPORT_INVALID_FILE(
            "GRAMMAR_E002",
            "grammar.import.failed.title",
            400
    ),
    GRAMMAR_NOT_FOUND(
            "GRAMMAR_E003",
            "grammar.not.found.title",
            404
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    GrammarErrorCode(String code, String titleKey, int statusCode) {
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
