package org.naho.file.exception;

import org.naho.i18n.message.file.FileTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum FileDomainErrorCode implements ErrorCode {
    FILE_ORIGINAL_NAME_EMPTY(
            "FILE_001",
            FileTitleMessageKey.FILE_NOT_VALID_TITLE,
            400
    ),
    FILE_OBJECT_KEY_EMPTY(
            "FILE_002",
            FileTitleMessageKey.FILE_NOT_VALID_TITLE,
            400
    ),
    FILE_SIZE_INVALID(
            "FILE_003",
            FileTitleMessageKey.FILE_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    FileDomainErrorCode(String code, String titleKey, int statusCode) {
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
