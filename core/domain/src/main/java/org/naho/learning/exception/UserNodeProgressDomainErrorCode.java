package org.naho.learning.exception;

import org.naho.i18n.message.learning.UserNodeProgressTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserNodeProgressDomainErrorCode implements ErrorCode {
    USER_NODE_PROGRESS_NODE_EMPTY(
            "USER_NODE_PROGRESS_001",
            UserNodeProgressTitleMessageKey.USER_NODE_PROGRESS_CREATION_FAILED_TITLE,
            400
    ),
    USER_NODE_PROGRESS_USER_EMPTY(
            "USER_NODE_PROGRESS_002",
            UserNodeProgressTitleMessageKey.USER_NODE_PROGRESS_CREATION_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserNodeProgressDomainErrorCode(String code, String titleKey, int statusCode) {
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
