package org.naho.question.exception;

import org.naho.i18n.message.question.VocabularyQuestionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum VocabularyQuestionErrorCode implements ErrorCode {
    VOCABULARY_QUESTION_NOT_FOUND("VOCABULARY_QUESTION_A001", VocabularyQuestionTitleMessageKey.VOCABULARY_QUESTION_NOT_FOUND_TITLE, 404);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    VocabularyQuestionErrorCode(String code, String titleKey, int statusCode) {
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
