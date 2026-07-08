package org.naho.file.model;

import org.naho.file.exception.FileDomainErrorCode;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.DomainException;

public class File {

    private final Long id;
    private final Long commentId;
    private final Long questionId;
    private final Long reportId;
    private final String objectKey;
    private final String originalName;
    private final String contentType;
    private final Long size;

    private File(Builder builder) {
        this.id = builder.id;
        this.commentId = builder.commentId;
        this.questionId = builder.questionId;
        this.reportId = builder.reportId;
        this.objectKey = builder.objectKey;
        this.originalName = builder.originalName;
        this.contentType = builder.contentType;
        this.size = builder.size;
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

    public Long getQuestionId() {
        return questionId;
    }

    public Long getReportId() {
        return reportId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public static class Builder {

        private Long id;
        private Long commentId;
        private Long questionId;
        private Long reportId;
        private String objectKey;
        private String originalName;
        private String contentType;
        private Long size;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder commentId(Long commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder reportId(Long reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder originalName(String originalName) {
            this.originalName = originalName;
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

        public File build() {
            if (objectKey == null || objectKey.isBlank()) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_OBJECT_KEY_EMPTY,
                        FileDetailMessageKey.FILE_EMPTY
                );
            }

            if (originalName == null || originalName.isBlank()) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_ORIGINAL_NAME_EMPTY,
                        FileDetailMessageKey.FILE_ORIGINAL_NAME_EMPTY
                );
            }

            if (size == null || size <= 0) {
                throw new DomainException(
                        FileDomainErrorCode.FILE_SIZE_INVALID,
                        FileDetailMessageKey.FILE_EMPTY
                );
            }

            return new File(this);
        }
    }
}