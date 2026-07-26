package org.naho.social.reaction.model;

import org.naho.i18n.message.social.ReactionDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.social.reaction.exception.ReactionDomainErrorCode;
import org.naho.social.reaction.type.ReactionType;

import java.time.Instant;

public class Reaction {

    private final Long id;
    private final Long userId;
    private final Long commentId;
    private final Long questionId;
    private final Instant createdTime;
    private final Instant modifiedTime;
    private ReactionType reactionType;

    private Reaction(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.commentId = builder.commentId;
        this.questionId = builder.questionId;
        this.reactionType = builder.reactionType;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
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
                .reactionType(this.reactionType)
                .createdTime(this.createdTime)
                .modifiedTime(this.modifiedTime);
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

    public Instant getCreatedTime() {
        return createdTime;
    }

    public Instant getModifiedTime() {
        return modifiedTime;
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private Long commentId;
        private Long questionId;
        private ReactionType reactionType;
        private Instant createdTime;
        private Instant modifiedTime;

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

        public Builder createdTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime(Instant modifiedTime) {
            this.modifiedTime = modifiedTime;
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
