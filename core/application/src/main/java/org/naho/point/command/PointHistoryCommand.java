package org.naho.point.command;

import org.naho.point.type.PointTransactionType;

public record PointHistoryCommand(
        Long id,
        Long userId,
        Long learningPathNodeId,
        Double point,
        PointTransactionType transactionType
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long userId;
        private Long learningPathNodeId;
        private Double point;
        private PointTransactionType transactionType;

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

        public PointHistoryCommand build() {
            return new PointHistoryCommand(
                    id,
                    userId,
                    learningPathNodeId,
                    point,
                    transactionType
            );
        }
    }
}
