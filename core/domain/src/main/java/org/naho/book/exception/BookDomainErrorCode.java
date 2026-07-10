package org.naho.book.exception;

import org.naho.i18n.message.book.BookTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum BookDomainErrorCode implements ErrorCode {
    BOOK_TITLE_EMPTY("BOOK_001", BookTitleMessageKey.BOOK_CREATION_FAILED_TITLE, 400),
    BOOK_JLPT_LEVEL_EMPTY("BOOK_002", BookTitleMessageKey.BOOK_CREATION_FAILED_TITLE, 400),
    BOOK_CEFR_LEVEL_EMPTY("BOOK_003", BookTitleMessageKey.BOOK_CREATION_FAILED_TITLE, 400),
    BOOK_ORDER_INDEX_EMPTY("BOOK_004", BookTitleMessageKey.BOOK_CREATION_FAILED_TITLE, 400);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    BookDomainErrorCode(String code, String titleKey, int statusCode) {
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
