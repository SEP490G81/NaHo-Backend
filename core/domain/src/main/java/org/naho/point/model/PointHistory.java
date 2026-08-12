package org.naho.point.model;

import org.naho.i18n.message.point.PointHistoryDetailMessageKey;
import org.naho.point.exception.PointHistoryDomainErrorCode;
import org.naho.point.type.PointTransactionType;
import org.naho.shared.exception.DomainException;

import java.time.Instant;

public class PointHistory {

    private final Long id;
    private final Long userId;

    private final Long learningPathNodeId;

    private Double point;
    private PointTransactionType transactionType;
    private Instant transactionTime;

    private PointHistory(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.learningPathNodeId = builder.learningPathNodeId;
        this.point = builder.point;
        this.transactionType = builder.transactionType;
        this.transactionTime = builder.transactionTime;
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

    public Long getLearningPathNodeId() {
        return learningPathNodeId;
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

    public void setTransactionTime(Instant transactionTime) {
        this.transactionTime = transactionTime;
    }

    public static final class Builder {

        private Long id;
        private Long userId;
        private Long learningPathNodeId;
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

        public Builder learningPathNodeId(Long learningPathNodeId) {
            this.learningPathNodeId = learningPathNodeId;
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

            if (point == null) {
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

            if (transactionType.equals(PointTransactionType.LEARNING_PATH_NODE_COMPLETION) && learningPathNodeId == null) {
                throw new DomainException(
                        PointHistoryDomainErrorCode.POINT_HISTORY_LEARNING_PATH_NODE_ID_NOT_VALID,
                        PointHistoryDetailMessageKey.POINT_HISTORY_LEARNING_PATH_NODE_ID_BLANK
                );
            }

            return new PointHistory(this);
        }
    }
}
