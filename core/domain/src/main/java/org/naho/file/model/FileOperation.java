package org.naho.file.model;

import org.naho.file.exception.FileDomainErrorCode;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.DomainException;

public class FileOperation {

    private final Long id;
    private final Long fileId;

    private OperationType operationType;
    private OperationStatus operationStatus;
    private Integer retryCount;

    private FileOperation(Builder builder) {
        this.id = builder.id;
        this.fileId = builder.fileId;
        this.operationType = builder.operationType;
        this.operationStatus = builder.operationStatus;
        this.retryCount = builder.retryCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getFileId() {
        return fileId;
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

    public void markCompleted() {
        this.operationStatus = OperationStatus.COMPLETED;
    }

    public void markFailed() {
        this.operationStatus = OperationStatus.FAILED;
    }

    public void increaseRetryCount() {
        this.retryCount++;
    }

    public static final class Builder {

        private Long id;
        private Long fileId;
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

        public FileOperation build() {
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

            if (fileId == null) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_ID_NULL,
                        FileDetailMessageKey.FILE_ID_NULL);
            }

            return new FileOperation(this);
        }
    }
}