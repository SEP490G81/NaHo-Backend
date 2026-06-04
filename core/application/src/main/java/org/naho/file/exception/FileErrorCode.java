package org.naho.file.exception;

import org.naho.i18n.message.file.FileTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum FileErrorCode implements ErrorCode {
    FILE_NOT_FOUND(
            "FILE_A001",
            FileTitleMessageKey.FILE_NOT_FOUND_TITLE
    ),
    FILE_UPLOAD_FAILED(
            "FILE_A002",
            FileTitleMessageKey.FILE_UPLOAD_FAILED_TITLE
    ),
    FILE_NOT_VALID(
            "FILE_A003",
            FileTitleMessageKey.FILE_NOT_VALID_TITLE
    ),
    FILE_DELETE_FAILED(
            "FILE_A004",
            FileTitleMessageKey.FILE_DELETE_FAILED_TITLE
    );

    private final String code;
    private final String titleKey;

    FileErrorCode(String code, String titleKey) {
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