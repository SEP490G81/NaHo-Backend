package org.naho.file.result;

import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

import java.time.Instant;

public class FileResult {

    private Long id;
    private String objectKey;
    private String accessUrl;
    private String originalFileName;
    private String contentType;
    private Long size;
    private String checksum;
    private OperationType operationType;
    private OperationStatus operationStatus;
    private Integer retryCount;
    private Instant nextRetryAt;

    public FileResult() {
    }

    public FileResult(Long id, String objectKey, String accessUrl, String originalFileName, String contentType, Long size, String checksum, OperationType operationType, OperationStatus operationStatus, Integer retryCount, Instant nextRetryAt) {
        this.id = id;
        this.objectKey = objectKey;
        this.accessUrl = accessUrl;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.size = size;
        this.checksum = checksum;
        this.operationType = operationType;
        this.operationStatus = operationStatus;
        this.retryCount = retryCount;
        this.nextRetryAt = nextRetryAt;
    }

    private FileResult(Builder builder) {
        this.id = builder.id;
        this.objectKey = builder.objectKey;
        this.accessUrl = builder.accessUrl;
        this.originalFileName = builder.originalFileName;
        this.contentType = builder.contentType;
        this.size = builder.size;
        this.checksum = builder.checksum;
        this.operationType = builder.operationType;
        this.operationStatus = builder.operationStatus;
        this.retryCount = builder.retryCount;
        this.nextRetryAt = builder.nextRetryAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(OperationStatus operationStatus) {
        this.operationStatus = operationStatus;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(Instant nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }

    public Long id() {
        return id;
    }

    public String objectKey() {
        return objectKey;
    }

    public String accessUrl() {
        return accessUrl;
    }

    public String originalFileName() {
        return originalFileName;
    }

    public String contentType() {
        return contentType;
    }

    public Long size() {
        return size;
    }

    public String checksum() {
        return checksum;
    }

    public OperationType operationType() {
        return operationType;
    }

    public OperationStatus operationStatus() {
        return operationStatus;
    }

    public Integer retryCount() {
        return retryCount;
    }

    public Instant nextRetryAt() {
        return nextRetryAt;
    }

    public static final class Builder {
        private Long id;
        private String objectKey;
        private String accessUrl;
        private String originalFileName;
        private String contentType;
        private Long size;
        private String checksum;
        private OperationType operationType;
        private OperationStatus operationStatus;
        private Integer retryCount;
        private Instant nextRetryAt;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder accessUrl(String accessUrl) {
            this.accessUrl = accessUrl;
            return this;
        }

        public Builder originalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        public Builder checksum(String checksum) {
            this.checksum = checksum;
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

        public Builder nextRetryAt(Instant nextRetryAt) {
            this.nextRetryAt = nextRetryAt;
            return this;
        }

        public FileResult build() {
            return new FileResult(this);
        }
    }
}
