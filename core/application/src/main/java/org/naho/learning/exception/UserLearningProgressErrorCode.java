package org.naho.learning.exception;

import org.naho.i18n.message.learning.UserLearningProgressTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum UserLearningProgressErrorCode implements ErrorCode {
    USER_LEARNING_PROGRESS_NOT_FOUND(
            "USER_LEARNING_PROGRESS_A001",
            "user_learning_progress.not_found.title",
            404
    ),
    USER_LEARNING_PROGRESS_ALREADY_EXISTS(
            "USER_LEARNING_PROGRESS_A002",
            UserLearningProgressTitleMessageKey.USER_LEARNING_PROGRESS_CREATION_FAILED_TITLE,
            400
    ),
    USER_LEARNING_PROGRESS_INVALID(
            "USER_LEARNING_PROGRESS_A003",
            UserLearningProgressTitleMessageKey.USER_LEARNING_PROGRESS_CREATION_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    UserLearningProgressErrorCode(String code, String titleKey, int statusCode) {
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
