package org.naho.social.reaction.exception;

import org.naho.i18n.message.social.CommentTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum CommentDomainErrorCode implements ErrorCode {
    COMMENT_USER_ID_NOT_VALID(
            "COMMENT_001",
            CommentTitleMessageKey.COMMENT_USER_ID_NOT_VALID_TITLE,
            400
    ),
    COMMENT_QUESTION_ID_NOT_VALID(
            "COMMENT_002",
            CommentTitleMessageKey.COMMENT_QUESTION_ID_NOT_VALID_TITLE,
            400
    ),
    COMMENT_CONTENT_NOT_VALID(
            "COMMENT_003",
            CommentTitleMessageKey.COMMENT_CONTENT_NOT_VALID_TITLE,
            400
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    CommentDomainErrorCode(String code, String titleKey, int statusCode) {
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
