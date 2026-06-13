package org.naho.file.model;

public class File {

    private final Long id;
    private final String objectKey;
    private final String originalName;
    private final String contentType;
    private final Long size;

    private File(Builder builder) {
        this.id = builder.id;
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
        private String objectKey;
        private String originalName;
        private String contentType;
        private Long size;

        public Builder id(Long id) {
            this.id = id;
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
            return new File(this);
        }
    }
}