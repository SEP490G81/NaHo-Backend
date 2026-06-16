package org.naho.social.exception;

import org.naho.i18n.message.social.ReactionTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum ReactionDomainErrorCode implements ErrorCode {
    REACTION_USER_ID_NOT_VALID(
            "REACTION_001",
            ReactionTitleMessageKey.REACTION_USER_ID_NOT_VALID_TITLE,
            400
    ),
    REACTION_COMMENT_ID_NOT_VALID(
            "REACTION_002",
            ReactionTitleMessageKey.REACTION_COMMENT_ID_NOT_VALID_TITLE,
            400
    ),
    REACTION_QUESTION_ID_NOT_VALID(
            "REACTION_004",
            ReactionTitleMessageKey.REACTION_QUESTION_ID_NOT_VALID_TITLE,
            400
    ),
    REACTION_TYPE_NOT_VALID(
            "REACTION_003",
            ReactionTitleMessageKey.REACTION_TYPE_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    ReactionDomainErrorCode(String code, String titleKey, int statusCode) {
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
