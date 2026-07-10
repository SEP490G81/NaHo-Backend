package org.naho.question.exception;

import org.naho.i18n.message.question.QuestionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum QuestionDomainErrorCode implements ErrorCode {
    QUESTION_TITLE_EMPTY("QST_D001", QuestionTitleMessageKey.QUESTION_CREATION_FAILED_TITLE, 400),
    QUESTION_DESCRIPTION_EMPTY("QST_D002", QuestionTitleMessageKey.QUESTION_CREATION_FAILED_TITLE, 400),
    QUESTION_ORDER_INDEX_EMPTY("QST_D003", QuestionTitleMessageKey.QUESTION_CREATION_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    QuestionDomainErrorCode(String code, String titleKey, int statusCode) {
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
