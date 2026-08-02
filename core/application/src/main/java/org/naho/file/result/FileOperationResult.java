package org.naho.file.result;

import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

public record FileOperationResult(
        Long id,
        Long fileId,
        OperationType operationType,
        OperationStatus operationStatus,
        Integer retryCount
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Long fileId;
        private OperationType operationType;
        private OperationStatus operationStatus;
        private Integer retryCount;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fileId(Long fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder operationType(OperationType operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder operationStatus(OperationStatus operationStatus) {
            this.operationStatus = operationStatus;
            return this;
        }

        public Builder retryCount(Integer retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public FileOperationResult build() {
            return new FileOperationResult(
                    id,
                    fileId,
                    operationType,
                    operationStatus,
                    retryCount
            );
        }
    }
}
