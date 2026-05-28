package org.naho.file.model;

public class File {

    private final Long id;
    private final String fileUrl;
    private final String previewUrl;
    private final String originalName;
    private final String contentType;
    private final Long size;

    private File(Builder builder) {
        this.id = builder.id;
        this.fileUrl = builder.fileUrl;
        this.previewUrl = builder.previewUrl;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
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
        private String fileUrl;
        private String previewUrl;
        private String originalName;
        private String contentType;
        private Long size;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        public Builder previewUrl(String previewUrl) {
            this.previewUrl = previewUrl;
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
            return new File(this);
        }
    }
}