package org.naho.learning.exception;

import org.naho.i18n.message.learning.UserLearningProgressTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserLearningProgressDomainErrorCode implements ErrorCode {
    USER_LEARNING_PROGRESS_LEARNING_PATH_NODE_EMPTY(
            "USER_LEARNING_PROGRESS_001",
            UserLearningProgressTitleMessageKey.USER_LEARNING_PROGRESS_CREATION_FAILED_TITLE,
            400
    ),
    USER_LEARNING_PROGRESS_USER_EMPTY(
            "USER_LEARNING_PROGRESS_002",
            UserLearningProgressTitleMessageKey.USER_LEARNING_PROGRESS_CREATION_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserLearningProgressDomainErrorCode(String code, String titleKey, int statusCode) {
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
