package org.naho.book.exception;

import org.naho.i18n.message.book.LessonTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum LessonDomainErrorCode implements ErrorCode {
    LESSON_ORDER_INDEX_EMPTY("LESSON_001", LessonTitleMessageKey.LESSON_CREATION_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    LessonDomainErrorCode(String code, String titleKey, int statusCode) {
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
