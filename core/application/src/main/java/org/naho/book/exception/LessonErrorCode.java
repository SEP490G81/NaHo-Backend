package org.naho.book.exception;

import org.naho.i18n.message.book.LessonTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum LessonErrorCode implements ErrorCode {
    LESSON_NOT_FOUND("LESSON_A001", LessonTitleMessageKey.LESSON_GET_FAIL_TITLE, 404),
    LESSON_UPDATE_FORBIDDEN("LESSON_A003", LessonTitleMessageKey.LESSON_UPDATE_FAILED_TITLE, 403);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    LessonErrorCode(String code, String titleKey, int statusCode) {
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
