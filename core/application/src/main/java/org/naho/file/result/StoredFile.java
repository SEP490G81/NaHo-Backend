package org.naho.file.result;

public class StoredFile {

    private Long commentId;
    private Long reportId;
    private String objectKey;
    private String bucketName;
    private String originalFileName;
    private String contentType;
    private Long size;
    private String checksum;

    public StoredFile() {
    }

    public StoredFile(Long commentId, Long reportId, String objectKey, String bucketName, String originalFileName, String contentType, Long size, String checksum) {
        this.commentId = commentId;
        this.reportId = reportId;
        this.objectKey = objectKey;
        this.bucketName = bucketName;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.size = size;
        this.checksum = checksum;
    }

    private StoredFile(Builder builder) {
        this.commentId = builder.commentId;
        this.reportId = builder.reportId;
        this.objectKey = builder.objectKey;
        this.bucketName = builder.bucketName;
        this.originalFileName = builder.originalFileName;
        this.contentType = builder.contentType;
        this.size = builder.size;
        this.checksum = builder.checksum;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
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

    // Accessors for backward compatibility
    public Long commentId() {
        return commentId;
    }

    public Long reportId() {
        return reportId;
    }

    public String objectKey() {
        return objectKey;
    }

    public String bucketName() {
        return bucketName;
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

    public static class Builder {
        private Long commentId;
        private Long reportId;
        private String objectKey;
        private String bucketName;
        private String originalFileName;
        private String contentType;
        private Long size;
        private String checksum;

        public Builder commentId(Long commentId) {
            this.commentId = commentId;
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

        public StoredFile build() {
            return new StoredFile(this);
        }
    }
}