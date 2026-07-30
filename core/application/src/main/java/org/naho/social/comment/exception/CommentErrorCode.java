package org.naho.social.comment.exception;

import org.naho.i18n.message.social.CommentTitleMessageKey;
import org.naho.shared.exception.ErrorCode;

public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND(
            "COMMENT_A001",
            CommentTitleMessageKey.COMMENT_GET_FAIL_TITLE,
            404
    ),
    COMMENT_NOT_AUTHORIZED(
            "COMMENT_A002",
            CommentTitleMessageKey.COMMENT_NOT_AUTHORIZED_TITLE,
            403
    );

    private final String code;
    private final String titleKey;
    private final int statusCode;

    CommentErrorCode(String code, String titleKey, int statusCode) {
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
