package org.naho.quote.exception;

import org.naho.i18n.message.quote.QuoteTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum QuoteErrorCode implements ErrorCode {

    QUOTE_NOT_FOUND(
            "QUOTE_E001",
            QuoteTitleMessageKey.QUOTE_NOT_FOUND_TITLE,
            404
    ),
    QUOTE_IMPORT_FAILED(
            "QUOTE_E002",
            QuoteTitleMessageKey.QUOTE_IMPORT_FAILED_TITLE,
            400
    ),
    QUOTE_IMPORT_EMPTY(
            "QUOTE_E003",
            QuoteTitleMessageKey.QUOTE_IMPORT_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    QuoteErrorCode(String code, String titleKey, int statusCode) {
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
