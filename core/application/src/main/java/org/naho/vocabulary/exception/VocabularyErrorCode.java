package org.naho.vocabulary.exception;

import org.naho.shared.exception.ErrorCode;

public enum VocabularyErrorCode implements ErrorCode {

    VOCABULARY_IMPORT_EMPTY(
            "VOCABULARY_E001",
            "vocabulary.import.failed.title",
            400
    ),
    VOCABULARY_IMPORT_INVALID_FILE(
            "VOCABULARY_E002",
            "vocabulary.import.failed.title",
            400
    ),
    VOCABULARY_EXPORT_NOT_FOUND(
            "VOCABULARY_E003",
            "vocabulary.export.not_found.title",
            404
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    VocabularyErrorCode(String code, String titleKey, int statusCode) {
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
