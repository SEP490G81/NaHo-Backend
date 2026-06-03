package org.naho.file.exception;

import org.naho.file.constant.FileApplicationMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum FileApplicationErrorCode implements ErrorCode {
    FILE_NOT_FOUND("FILE_A001", FileApplicationMessageKey.FILE_NOT_FOUND_TITLE),
    FILE_UPLOAD_FAILED("FILE_A002", FileApplicationMessageKey.FILE_UPLOAD_FAILED_TITLE);

    private final String code;
    private final String titleKey;

    FileApplicationErrorCode(String code, String titleKey) {
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