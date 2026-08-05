package org.naho.file.command;

import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

public class UpdateOperationStatusCommand {

    private Long fileOperationId;
    private String objectKey;
    private OperationType operationType;
    private OperationStatus toOperationStatus;
    private boolean isRetry;

    public UpdateOperationStatusCommand() {
    }

    public UpdateOperationStatusCommand(
            Long fileOperationId, String objectKey, OperationType operationType,
            OperationStatus toOperationStatus, boolean isRetry
    ) {
        this.fileOperationId = fileOperationId;
        this.objectKey = objectKey;
        this.operationType = operationType;
        this.toOperationStatus = toOperationStatus;
        this.isRetry = isRetry;
    }

    public UpdateOperationStatusCommand(Long fileOperationId, String objectKey, OperationType operationType,
                                        OperationStatus toOperationStatus) {
        this(fileOperationId, objectKey, operationType, toOperationStatus, false);
    }

    private UpdateOperationStatusCommand(Builder builder) {
        this.fileOperationId = builder.fileOperationId;
        this.objectKey = builder.objectKey;
        this.operationType = builder.operationType;
        this.toOperationStatus = builder.toOperationStatus;
        this.isRetry = builder.isRetry;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getFileOperationId() {
        return fileOperationId;
    }

    public void setFileOperationId(Long fileOperationId) {
        this.fileOperationId = fileOperationId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public OperationStatus getToOperationStatus() {
        return toOperationStatus;
    }

    public void setToOperationStatus(OperationStatus toOperationStatus) {
        this.toOperationStatus = toOperationStatus;
    }

    public boolean isRetry() {
        return isRetry;
    }

    public void setRetry(boolean retry) {
        isRetry = retry;
    }

    public Long fileOperationId() {
        return fileOperationId;
    }

    public String objectKey() {
        return objectKey;
    }

    public OperationType operationType() {
        return operationType;
    }

    public OperationStatus toOperationStatus() {
        return toOperationStatus;
    }

    public static final class Builder {
        private Long fileOperationId;
        private String objectKey;
        private OperationType operationType;
        private OperationStatus toOperationStatus;
        private boolean isRetry;

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

        public Builder isRetry(boolean isRetry) {
            this.isRetry = isRetry;
            return this;
        }

        public UpdateOperationStatusCommand build() {
            return new UpdateOperationStatusCommand(this);
        }
    }
}
