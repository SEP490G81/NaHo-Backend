package org.naho.social.model;

import org.naho.i18n.message.social.ReactionDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.social.exception.ReactionDomainErrorCode;
import org.naho.social.type.ReactionType;

public class Reaction {

    private final Long id;
    private final Long userId;
    private final Long commentId;
    private final Long questionId;
    private ReactionType reactionType;

    private Reaction(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.commentId = builder.commentId;
        this.questionId = builder.questionId;
        this.reactionType = builder.reactionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .userId(this.userId)
                .commentId(this.commentId)
                .questionId(this.questionId)
                .reactionType(this.reactionType);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private Long commentId;
        private Long questionId;
        private ReactionType reactionType;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder commentId(Long commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder reactionType(ReactionType reactionType) {
            this.reactionType = reactionType;
            return this;
        }

        public Reaction build() {

            if (userId == null) {
                throw new DomainException(
                        ReactionDomainErrorCode.REACTION_USER_ID_NOT_VALID,
                        ReactionDetailMessageKey.REACTION_USER_ID_BLANK
                );
            }

            if (commentId == null && questionId == null) {
                throw new DomainException(
                        ReactionDomainErrorCode.REACTION_COMMENT_ID_NOT_VALID,
                        ReactionDetailMessageKey.REACTION_COMMENT_ID_BLANK
                );
            }

            if (commentId != null && questionId != null) {
                throw new DomainException(
                        ReactionDomainErrorCode.REACTION_COMMENT_ID_NOT_VALID,
                        "reaction.target.invalid"
                );
            }

            if (reactionType == null) {
                throw new DomainException(
                        ReactionDomainErrorCode.REACTION_TYPE_NOT_VALID,
                        ReactionDetailMessageKey.REACTION_TYPE_BLANK
                );
            }

            return new Reaction(this);
        }
    }
}