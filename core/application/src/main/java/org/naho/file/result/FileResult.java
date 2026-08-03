package org.naho.file.result;

import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

public class FileResult {

    private Long id;
    private String accessUrl;
    private String originalFileName;
    private String contentType;
    private Long size;
    private String checksum;
    private OperationType operationType;
    private OperationStatus operationStatus;
    private Integer retryCount;

    public FileResult() {
    }

    public FileResult(Long id, String accessUrl, String originalFileName, String contentType, Long size, String checksum, OperationType operationType, OperationStatus operationStatus, Integer retryCount) {
        this.id = id;
        this.accessUrl = accessUrl;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.size = size;
        this.checksum = checksum;
        this.operationType = operationType;
        this.operationStatus = operationStatus;
        this.retryCount = retryCount;
    }

    private FileResult(Builder builder) {
        this.id = builder.id;
        this.accessUrl = builder.accessUrl;
        this.originalFileName = builder.originalFileName;
        this.contentType = builder.contentType;
        this.size = builder.size;
        this.checksum = builder.checksum;
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

    public void setId(Long id) {
        this.id = id;
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

    public Long id() {
        return id;
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

    public static final class Builder {
        private Long id;
        private String accessUrl;
        private String originalFileName;
        private String contentType;
        private Long size;
        private String checksum;
        private OperationType operationType;
        private OperationStatus operationStatus;
        private Integer retryCount;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
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

        public FileResult build() {
            return new FileResult(this);
        }
    }
}
