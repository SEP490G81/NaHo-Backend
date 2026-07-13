package org.naho.social.model;

import org.naho.i18n.message.social.CommentDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.social.exception.CommentDomainErrorCode;

public class Comment {

    private final Long id;
    private final Long userId;
    private final Long questionId;
    private final Long parentId;
    private final String content;

    private Comment(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.questionId = builder.questionId;
        this.parentId = builder.parentId;
        this.content = builder.content;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getContent() {
        return content;
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private Long questionId;
        private Long parentId;
        private String content;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder parentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Comment build() {

            if (userId == null) {
                throw new DomainException(
                        CommentDomainErrorCode.COMMENT_USER_ID_NOT_VALID,
                        CommentDetailMessageKey.COMMENT_USER_ID_BLANK
                );
            }

            if (questionId == null) {
                throw new DomainException(
                        CommentDomainErrorCode.COMMENT_QUESTION_ID_NOT_VALID,
                        CommentDetailMessageKey.COMMENT_QUESTION_ID_BLANK
                );
            }

            if (content == null || content.isBlank()) {
                throw new DomainException(
                        CommentDomainErrorCode.COMMENT_CONTENT_NOT_VALID,
                        CommentDetailMessageKey.COMMENT_CONTENT_BLANK
                );
            }

            return new Comment(this);
        }
    }
}