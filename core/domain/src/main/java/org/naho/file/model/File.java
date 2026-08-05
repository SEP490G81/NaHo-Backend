package org.naho.file.model;

import org.naho.file.exception.FileDomainErrorCode;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.file.valueobject.NextRetryAt;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.DomainException;

public class File {

    private final Long id;
    private final Long commentId;
    private final Long reportId;
    private final Long userId;
    private final Long personaId;
    private final Long leagueId;
    private final Long speakingQuestionId;

    private final String objectKey;
    private final String bucketName;
    private final String originalFileName;
    private final String contentType;
    private final Long size;
    private final String checksum;

    private OperationType operationType;
    private OperationStatus operationStatus;

    private Integer retryCount;
    private NextRetryAt nextRetryAt;

    private File(Builder builder) {
        this.id = builder.id;
        this.commentId = builder.commentId;
        this.reportId = builder.reportId;
        this.userId = builder.userId;
        this.personaId = builder.personaId;
        this.leagueId = builder.leagueId;
        this.speakingQuestionId = builder.speakingQuestionId;
        this.objectKey = builder.objectKey;
        this.bucketName = builder.bucketName;
        this.originalFileName = builder.originalFileName;
        this.contentType = builder.contentType;
        this.size = builder.size;
        this.checksum = builder.checksum;
        this.operationType = builder.operationType;
        this.operationStatus = builder.operationStatus;
        this.retryCount = builder.retryCount != null ? builder.retryCount : 0;
        this.nextRetryAt = builder.nextRetryAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getReportId() {
        return reportId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public Long getSpeakingQuestionId() {
        return speakingQuestionId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public String getChecksum() {
        return checksum;
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

    public NextRetryAt getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(NextRetryAt nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }

    public void markCompleted() {
        this.operationStatus = OperationStatus.COMPLETED;
        this.nextRetryAt = null;
    }

    public void markFailed() {
        this.operationStatus = OperationStatus.FAILED;
    }

    public void markRetryLimitExceeded() {
        this.operationStatus = OperationStatus.RETRY_LIMIT_EXCEEDED;
        this.nextRetryAt = null;
    }

    public int incrementRetryCount() {
        this.retryCount += 1;
        return this.retryCount;
    }

    public static class Builder {
        private Long id;
        private Long commentId;
        private Long reportId;
        private Long userId;
        private Long personaId;
        private Long leagueId;
        private Long speakingQuestionId;
        private String objectKey;
        private String bucketName;
        private String originalFileName;
        private String contentType;
        private Long size;
        private String checksum;
        private OperationType operationType;
        private OperationStatus operationStatus;
        private Integer retryCount;
        private NextRetryAt nextRetryAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder commentId(Long commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder reportId(Long reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder personaId(Long personaId) {
            this.personaId = personaId;
            return this;
        }

        public Builder leagueId(Long leagueId) {
            this.leagueId = leagueId;
            return this;
        }

        public Builder speakingQuestionId(Long speakingQuestionId) {
            this.speakingQuestionId = speakingQuestionId;
            return this;
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
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

        public Builder nextRetryAt(NextRetryAt nextRetryAt) {
            this.nextRetryAt = nextRetryAt;
            return this;
        }

        public File build() {
            if (objectKey == null || objectKey.isBlank()) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_OBJECT_KEY_EMPTY,
                        FileDetailMessageKey.FILE_EMPTY);
            }

            if (originalFileName == null || originalFileName.isBlank()) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_ORIGINAL_NAME_EMPTY,
                        FileDetailMessageKey.FILE_ORIGINAL_NAME_EMPTY);
            }

            if (size == null || size <= 0) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_SIZE_INVALID,
                        FileDetailMessageKey.FILE_EMPTY);
            }

            if (retryCount == null) {
                retryCount = 0;
            }

            return new File(this);
        }
    }
}