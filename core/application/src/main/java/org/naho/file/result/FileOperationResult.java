package org.naho.file.result;

import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

public record FileOperationResult(
        OperationType operationType,
        OperationStatus operationStatus
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OperationType operationType;
        private OperationStatus operationStatus;

        public Builder operationType(OperationType operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder operationStatus(OperationStatus operationStatus) {
            this.operationStatus = operationStatus;
            return this;
        }

        public FileOperationResult build() {
            return new FileOperationResult(
                    operationType,
                    operationStatus
            );
        }
    }
}
