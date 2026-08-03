package org.naho.file.result;

public class FileResult {

    private Long id;
    private String accessUrl;
    private String originalFileName;
    private String contentType;
    private Long size;
    private FileOperationResult fileOperation;

    public FileResult() {
    }

    public FileResult(Long id, String accessUrl, String originalFileName, String contentType, Long size, FileOperationResult fileOperation) {
        this.id = id;
        this.accessUrl = accessUrl;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.size = size;
        this.fileOperation = fileOperation;
    }

    private FileResult(Builder builder) {
        this.id = builder.id;
        this.accessUrl = builder.accessUrl;
        this.originalFileName = builder.originalFileName;
        this.contentType = builder.contentType;
        this.size = builder.size;
        this.fileOperation = builder.fileOperation;
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

    public FileOperationResult getFileOperation() {
        return fileOperation;
    }

    public void setFileOperation(FileOperationResult fileOperation) {
        this.fileOperation = fileOperation;
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

    public FileOperationResult fileOperation() {
        return fileOperation;
    }

    public static final class Builder {
        private Long id;
        private String accessUrl;
        private String originalFileName;
        private String contentType;
        private Long size;
        private FileOperationResult fileOperation;

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

        public Builder fileOperation(FileOperationResult fileOperation) {
            this.fileOperation = fileOperation;
            return this;
        }

        public FileResult build() {
            return new FileResult(this);
        }
    }
}
