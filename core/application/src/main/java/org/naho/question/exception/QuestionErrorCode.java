package org.naho.question.exception;

import org.naho.i18n.message.question.QuestionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum QuestionErrorCode implements ErrorCode {
    QUESTION_NOT_FOUND("QST_A001", QuestionTitleMessageKey.QUESTION_NOT_FOUND_TITLE, 404),
    QUESTION_UPDATE_FORBIDDEN("QST_A002", QuestionTitleMessageKey.QUESTION_UPDATE_FAILED_TITLE, 403),
    QUESTION_DELETE_FORBIDDEN("QST_A003", QuestionTitleMessageKey.QUESTION_DELETE_FAILED_TITLE, 403),
    QUESTION_ORDER_INDEX_INVALID("QST_A004", QuestionTitleMessageKey.QUESTION_CREATION_FAILED_TITLE, 400),
    QUESTION_TITLE_ALREADY_EXISTS("QST_A005", QuestionTitleMessageKey.QUESTION_TITLE_ALREADY_EXISTS_TITLE, 400);

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
