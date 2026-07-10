package org.naho.learning.exception;

import org.naho.i18n.message.learning.LearningPathNodeTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum LearningPathNodeDomainErrorCode implements ErrorCode {
    LEARNING_PATH_NODE_OBJECTIVE_EMPTY(
            "LEARNING_PATH_NODE_001",
            LearningPathNodeTitleMessageKey.LEARNING_PATH_NODE_CREATION_FAILED_TITLE,
            400
    ),
    LEARNING_PATH_NODE_ORDER_INDEX_EMPTY(
            "LEARNING_PATH_NODE_002",
            LearningPathNodeTitleMessageKey.LEARNING_PATH_NODE_CREATION_FAILED_TITLE,
            400
    ),
    LEARNING_PATH_NODE_TYPE_EMPTY(
            "LEARNING_PATH_NODE_003",
            LearningPathNodeTitleMessageKey.LEARNING_PATH_NODE_CREATION_FAILED_TITLE,
            400
    ),
    LEARNING_PATH_NODE_SPEAKING_QUESTION_EMPTY(
            "LEARNING_PATH_NODE_004",
            LearningPathNodeTitleMessageKey.LEARNING_PATH_NODE_CREATION_FAILED_TITLE,
            400
    ),
    LEARNING_PATH_NODE_VOCABULARY_QUESTION_EMPTY(
            "LEARNING_PATH_NODE_005",
            LearningPathNodeTitleMessageKey.LEARNING_PATH_NODE_CREATION_FAILED_TITLE,
            400
    ),
    LEARNING_PATH_NODE_CHEST_EMPTY(
            "LEARNING_PATH_NODE_006",
            LearningPathNodeTitleMessageKey.LEARNING_PATH_NODE_CREATION_FAILED_TITLE,
            400
    ),
    LEARNING_PATH_NODE_GLOBAL_ORDER_INDEX_EMPTY(
            "LEARNING_PATH_NODE_007",
            LearningPathNodeTitleMessageKey.LEARNING_PATH_NODE_CREATION_FAILED_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    LearningPathNodeDomainErrorCode(String code, String titleKey, int statusCode) {
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
