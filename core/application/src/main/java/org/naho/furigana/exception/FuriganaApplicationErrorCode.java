package org.naho.furigana.exception;

import org.naho.i18n.message.furigana.FuriganaTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum FuriganaApplicationErrorCode implements ErrorCode {
    FURIGANA_ANALYZE_FAILED(
            "FURI_A001",
            FuriganaTitleMessageKey.FURIGANA_ANALYZE_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    FuriganaApplicationErrorCode(String code, String titleKey, int statusCode) {
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
