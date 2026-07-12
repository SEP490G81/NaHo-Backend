package org.naho.question.exception;

import org.naho.i18n.message.question.SpeakingQuestionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum SpeakingQuestionErrorCode implements ErrorCode {
    SPEAKING_QUESTION_NOT_FOUND("QST_A001", SpeakingQuestionTitleMessageKey.SPEAKING_QUESTION_NOT_FOUND_TITLE, 404),
    SPEAKING_QUESTION_UPDATE_FORBIDDEN("QST_A002", SpeakingQuestionTitleMessageKey.SPEAKING_QUESTION_UPDATE_FAILED_TITLE, 403),
    SPEAKING_QUESTION_DELETE_FORBIDDEN("QST_A003", SpeakingQuestionTitleMessageKey.SPEAKING_QUESTION_DELETE_FAILED_TITLE, 403),
    SPEAKING_QUESTION_TITLE_ALREADY_EXISTS("QST_A005", SpeakingQuestionTitleMessageKey.SPEAKING_QUESTION_TITLE_ALREADY_EXISTS_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    SpeakingQuestionErrorCode(String code, String titleKey, int statusCode) {
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
