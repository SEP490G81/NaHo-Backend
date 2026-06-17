package org.naho.question.exeption;

import org.naho.i18n.message.speech.QuestionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum QuestionErrorCode implements ErrorCode {
    QUESTION_NOT_FOUND("QST_A001", QuestionTitleMessageKey.QUESTION_NOT_FOUND_TITLE, 404);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    QuestionErrorCode(String code, String titleKey, int statusCode) {
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
