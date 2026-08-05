package org.naho.file.exception;

import org.naho.i18n.message.file.FileTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum FileErrorCode implements ErrorCode {
    FILE_NOT_FOUND(
            "FILE_A001",
            FileTitleMessageKey.FILE_NOT_FOUND_TITLE,
            404
    ),
    FILE_UPLOAD_FAILED(
            "FILE_A002",
            FileTitleMessageKey.FILE_UPLOAD_FAILED_TITLE,
            500
    ),
    FILE_NOT_VALID(
            "FILE_A003",
            FileTitleMessageKey.FILE_NOT_VALID_TITLE,
            400
    ),
    FILE_DELETE_FAILED(
            "FILE_A004",
            FileTitleMessageKey.FILE_DELETE_FAILED_TITLE,
            500
    ),
    FILE_GENERATE_PRESIGNED_URL_FAILED(
            "FILE_A005",
            FileTitleMessageKey.FILE_GENERATE_PRESIGNED_URL_FAILED_TITLE,
            500
    ),
    FILE_DOWNLOAD_FAILED(
            "FILE_A006",
            FileTitleMessageKey.FILE_DOWNLOAD_FAILED_TITLE,
            500
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    FileErrorCode(String code, String titleKey, int statusCode) {
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