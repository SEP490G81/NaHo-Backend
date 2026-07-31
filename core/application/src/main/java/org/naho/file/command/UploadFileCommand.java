package org.naho.file.command;

import java.io.InputStream;

public class UploadFileCommand {

    private String objectKey;
    private InputStream inputStream;
    private String contentType;
    private Long size;

    private UploadFileCommand(Builder builder) {
        this.objectKey = builder.objectKey;
        this.inputStream = builder.inputStream;
        this.contentType = builder.contentType;
        this.size = builder.size;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
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

    public static final class Builder {
        private String objectKey;
        private InputStream inputStream;
        private String contentType;
        private Long size;

        private Builder() {
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
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

        public UploadFileCommand build() {
            return new UploadFileCommand(this);
        }
    }
}
