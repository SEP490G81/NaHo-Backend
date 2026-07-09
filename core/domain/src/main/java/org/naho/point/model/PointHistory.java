package org.naho.point.model;

import org.naho.i18n.message.point.PointHistoryDetailMessageKey;
import org.naho.point.exception.PointHistoryDomainErrorCode;
import org.naho.point.type.PointTransactionType;
import org.naho.shared.exception.DomainException;

import java.time.Instant;

public class PointHistory {

    private final Long id;
    private final Long userId;

    private final Long questionId;
    private final Long objectiveId;
    private final Long lessonId;
    private final Long topicId;

    private Double point;
    private PointTransactionType transactionType;
    private Instant transactionTime;

    private PointHistory(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.questionId = builder.questionId;
        this.objectiveId = builder.objectiveId;
        this.lessonId = builder.lessonId;
        this.topicId = builder.topicId;
        this.point = builder.point;
        this.transactionType = builder.transactionType;
        this.transactionTime = builder.transactionTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private Long userId;
        private Long questionId;
        private Long objectiveId;
        private Long lessonId;
        private Long topicId;
        private Double point;
        private PointTransactionType transactionType;
        private Instant transactionTime;

        private Builder() {
        }

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

        public Builder objectiveId(Long objectiveId) {
            this.objectiveId = objectiveId;
            return this;
        }

        public Builder lessonId(Long lessonId) {
            this.lessonId = lessonId;
            return this;
        }

        public Builder topicId(Long topicId) {
            this.topicId = topicId;
            return this;
        }

        public Builder point(Double point) {
            this.point = point;
            return this;
        }

        public Builder transactionType(PointTransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder transactionTime(Instant transactionTime) {
            this.transactionTime = transactionTime;
            return this;
        }

        public PointHistory build() {
            if (userId == null) {
                throw new DomainException(
                        PointHistoryDomainErrorCode.POINT_HISTORY_USER_ID_NOT_VALID,
                        PointHistoryDetailMessageKey.POINT_HISTORY_USER_ID_BLANK
                );
            }

            if (point == null || point == 0) {
                throw new DomainException(
                        PointHistoryDomainErrorCode.POINT_HISTORY_POINT_NOT_VALID,
                        PointHistoryDetailMessageKey.POINT_HISTORY_POINT_INVALID
                );
            }

            if (transactionType == null) {
                throw new DomainException(
                        PointHistoryDomainErrorCode.POINT_HISTORY_TRANSACTION_TYPE_NOT_VALID,
                        PointHistoryDetailMessageKey.POINT_HISTORY_TRANSACTION_TYPE_BLANK
                );
            }

            if (transactionTime == null) {
                throw new DomainException(
                        PointHistoryDomainErrorCode.POINT_HISTORY_TRANSACTION_TIME_NOT_VALID,
                        PointHistoryDetailMessageKey.POINT_HISTORY_TRANSACTION_TIME_BLANK
                );
            }

            if (transactionType.equals(PointTransactionType.QUESTION_COMPLETION) && questionId == null) {
                throw new DomainException(
                        PointHistoryDomainErrorCode.POINT_HISTORY_QUESTION_ID_NOT_VALID,
                        PointHistoryDetailMessageKey.POINT_HISTORY_QUESTION_ID_BLANK
                );
            }

            if (transactionType.equals(PointTransactionType.CAN_DO_COMPLETION) && objectiveId == null) {
                throw new DomainException(
                        PointHistoryDomainErrorCode.POINT_HISTORY_OBJECTIVE_ID_NOT_VALID,
                        PointHistoryDetailMessageKey.POINT_HISTORY_OBJECTIVE_ID_BLANK
                );
            }

            if (transactionType.equals(PointTransactionType.LESSON_COMPLETION) && lessonId == null) {
                throw new DomainException(
                        PointHistoryDomainErrorCode.POINT_HISTORY_LESSON_ID_NOT_VALID,
                        PointHistoryDetailMessageKey.POINT_HISTORY_LESSON_ID_BLANK
                );
            }

            if (transactionType.equals(PointTransactionType.TOPIC_COMPLETION) && topicId == null) {
                throw new DomainException(
                        PointHistoryDomainErrorCode.POINT_HISTORY_TOPIC_ID_NOT_VALID,
                        PointHistoryDetailMessageKey.POINT_HISTORY_TOPIC_ID_BLANK
                );
            }

            return new PointHistory(this);
        }
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

    public Long getObjectiveId() {
        return objectiveId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public Double getPoint() {
        return point;
    }

    public PointTransactionType getTransactionType() {
        return transactionType;
    }

    public Instant getTransactionTime() {
        return transactionTime;
    }
}
