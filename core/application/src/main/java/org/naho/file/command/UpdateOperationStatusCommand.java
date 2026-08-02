package org.naho.file.command;

import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

public record UpdateOperationStatusCommand(
        Long fileOperationId,
        String objectKey,
        OperationType operationType,
        OperationStatus toOperationStatus
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long fileOperationId;
        private String objectKey;
        private OperationType operationType;
        private OperationStatus toOperationStatus;

        private Builder() {
        }

        public Builder fileOperationId(Long fileOperationId) {
            this.fileOperationId = fileOperationId;
            return this;
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder operationType(OperationType operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder toOperationStatus(OperationStatus toOperationStatus) {
            this.toOperationStatus = toOperationStatus;
            return this;
        }

        public UpdateOperationStatusCommand build() {
            return new UpdateOperationStatusCommand(
                    fileOperationId,
                    objectKey,
                    operationType,
                    toOperationStatus
            );
        }
    }
}
