package org.naho.file.exception;

import org.naho.i18n.message.file.FileOperationTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum FileOperationErrorCode implements ErrorCode {
    FILE_OPERATION_NOT_FOUND(
            "FILE_OPERATION_A001",
            FileOperationTitleMessageKey.FILE_OPERATION_NOT_FOUND_TITLE,
            404
    ),
    FILE_OPERATION_NOT_VALID(
            "FILE_OPERATION_A002",
            FileOperationTitleMessageKey.FILE_OPERATION_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    FileOperationErrorCode(String code, String titleKey, int statusCode) {
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
