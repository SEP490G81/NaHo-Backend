package org.naho.learning.exception;

import org.naho.i18n.message.learning.UserNodeProgressTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserNodeProgressErrorCode implements ErrorCode {
    USER_NODE_PROGRESS_NOT_FOUND(
            "USER_NODE_PROGRESS_A001",
            UserNodeProgressTitleMessageKey.USER_NODE_PROGRESS_NOT_FOUND_TITLE,
            404
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserNodeProgressErrorCode(String code, String titleKey, int statusCode) {
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
