package org.naho.file.command;

import java.io.InputStream;

public class FileUploadCommand {

    private String folderName;
    private String originalName;
    private InputStream inputStream;
    private String contentType;
    private Long size;

    private FileUploadCommand(Builder builder) {
        this.folderName = builder.folderName;
        this.originalName = builder.originalName;
        this.inputStream = builder.inputStream;
        this.contentType = builder.contentType;
        this.size = builder.size;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String folderName;
        private String originalName;
        private InputStream inputStream;
        private String contentType;
        private Long size;

        private Builder() {
        }

        public Builder folderName(String folderName) {
            this.folderName = folderName;
            return this;
        }

        public Builder originalName(String originalName) {
            this.originalName = originalName;
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

        public FileUploadCommand build() {
            return new FileUploadCommand(this);
        }
    }
}
