package org.naho.book.exception;

import org.naho.i18n.message.book.BookTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum BookErrorCode implements ErrorCode {
    BOOK_NOT_FOUND("BOOK_A001", BookTitleMessageKey.BOOK_GET_FAIL_TITLE, 404);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    BookErrorCode(String code, String titleKey, int statusCode) {
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
