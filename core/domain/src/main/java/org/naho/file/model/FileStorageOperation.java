package org.naho.file.model;

import org.naho.file.exception.FileDomainErrorCode;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.DomainException;

public class FileStorageOperation {

    private final Long id;
    private final Long fileId;
    private final String objectKey;

    private OperationType operationType;
    private OperationStatus operationStatus;
    private Integer retryCount;

    private FileStorageOperation(Builder builder) {
        this.id = builder.id;
        this.fileId = builder.fileId;
        this.objectKey = builder.objectKey;
        this.operationType = builder.operationType;
        this.operationStatus = builder.operationStatus;
        this.retryCount = builder.retryCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private Long fileId;
        private String objectKey;
        private OperationType operationType;
        private OperationStatus operationStatus;
        private Integer retryCount = 0;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fileId(Long fileId) {
            this.fileId = fileId;
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

        public Builder operationStatus(OperationStatus operationStatus) {
            this.operationStatus = operationStatus;
            return this;
        }

        public Builder retryCount(Integer retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public FileStorageOperation build() {
            if (operationType == null) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_OPERATION_TYPE_EMPTY,
                        FileDetailMessageKey.FILE_OPERATION_TYPE_EMPTY);
            }

            if (operationStatus == null) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_OPERATION_STATUS_EMPTY,
                        FileDetailMessageKey.FILE_OPERATION_STATUS_EMPTY);
            }

            if (retryCount == null) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_RETRY_COUNT_EMPTY,
                        FileDetailMessageKey.FILE_RETRY_COUNT_EMPTY);
            }

            if (operationType.equals(OperationType.UPLOAD) && fileId == null) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_ID_NULL,
                        FileDetailMessageKey.FILE_ID_NULL);
            }

            if (operationType.equals(OperationType.DELETE) &&
                    (objectKey == null || objectKey.isBlank())) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_OBJECT_KEY_EMPTY,
                        FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY);
            }

            return new FileStorageOperation(this);
        }
    }

    // Getters...

    public Long getId() {
        return id;
    }

    public Long getFileId() {
        return fileId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    // State update methods

    public void markCompleted() {
        this.operationStatus = OperationStatus.COMPLETED;
    }

    public void markFailed() {
        this.operationStatus = OperationStatus.FAILED;
    }

    public void increaseRetryCount() {
        this.retryCount++;
    }
}