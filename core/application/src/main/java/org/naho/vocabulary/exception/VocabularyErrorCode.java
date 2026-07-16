package org.naho.vocabulary.exception;

import org.naho.i18n.message.question.VocabularyQuestionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum VocabularyErrorCode implements ErrorCode {

    VOCABULARY_IMPORT_EMPTY(
            "VOCABULARY_E001",
            VocabularyQuestionTitleMessageKey.VOCABULARY_IMPORT_FAILED_TITLE,
            400
    ),
    VOCABULARY_IMPORT_INVALID_FILE(
            "VOCABULARY_E002",
            VocabularyQuestionTitleMessageKey.VOCABULARY_IMPORT_FAILED_TITLE,
            400
    ),
    VOCABULARY_EXPORT_NOT_FOUND(
            "VOCABULARY_E003",
            VocabularyQuestionTitleMessageKey.VOCABULARY_EXPORT_NOT_FOUND_TITLE,
            404
    ),
    VOCABULARY_NOT_FOUND(
            "VOCABULARY_E004",
            VocabularyQuestionTitleMessageKey.VOCABULARY_NOT_FOUND_TITLE,
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
