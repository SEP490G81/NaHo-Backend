package org.naho.furigana.exception;

import org.naho.furigana.constant.FuriganaApplicationMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum FuriganaApplicationErrorCode implements ErrorCode {
    FURIGANA_ANALYZE_FAILED("FURI_A001", FuriganaApplicationMessageKey.FURIGANA_ANALYZE_FAILED_TITLE);

    private final String code;
    private final String titleKey;

    FuriganaApplicationErrorCode(String code, String titleKey) {
        this.code = code;
        this.titleKey = titleKey;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitleKey() {
        return titleKey;
    }
}
