package org.naho.file.result;

import java.io.InputStream;

public record DownloadedFile(
        String fileName,
        String contentType,
        Long contentLength,
        InputStream inputStream
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String fileName;
        private String contentType;
        private Long contentLength;
        private InputStream inputStream;

        private Builder() {
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder contentLength(Long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public DownloadedFile build() {
            return new DownloadedFile(
                    fileName,
                    contentType,
                    contentLength,
                    inputStream
            );
        }
    }
}
