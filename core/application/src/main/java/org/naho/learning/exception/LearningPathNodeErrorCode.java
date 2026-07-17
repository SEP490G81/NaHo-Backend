package org.naho.learning.exception;

import org.naho.i18n.message.learning.LearningPathNodeTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum LearningPathNodeErrorCode implements ErrorCode {
    LEARNING_PATH_NODE_NOT_FOUND("LEARNING_PATH_NODE_A001",
            LearningPathNodeTitleMessageKey.LEARNING_PATH_NODE_NOT_FOUND_TITLE, 404);

    private final String code;
    private final String titleKey;
    private final int statusCode;

    LearningPathNodeErrorCode(String code, String titleKey, int statusCode) {
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
