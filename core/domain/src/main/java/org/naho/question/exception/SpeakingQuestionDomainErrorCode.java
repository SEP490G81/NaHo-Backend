package org.naho.question.exception;

import org.naho.i18n.message.question.SpeakingQuestionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum SpeakingQuestionDomainErrorCode implements ErrorCode {
    SPEAKING_QUESTION_TITLE_EMPTY("QST_D001", SpeakingQuestionTitleMessageKey.SPEAKING_QUESTION_CREATION_FAILED_TITLE, 400),
    SPEAKING_QUESTION_DESCRIPTION_EMPTY("QST_D002", SpeakingQuestionTitleMessageKey.SPEAKING_QUESTION_CREATION_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    SpeakingQuestionDomainErrorCode(String code, String titleKey, int statusCode) {
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
