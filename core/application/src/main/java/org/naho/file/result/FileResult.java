package org.naho.file.result;

public record FileResult(
        Long id,
        String localStoragePath,
        String objectKey,
        String originalFileName,
        String contentType,
        Long size
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String localStoragePath;
        private String objectKey;
        private String originalFileName;
        private String contentType;
        private Long size;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder localStoragePath(String localStoragePath) {
            this.localStoragePath = localStoragePath;
            return this;
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
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

        public FileResult build() {
            return new FileResult(
                    id,
                    localStoragePath,
                    objectKey,
                    originalFileName,
                    contentType,
                    size
            );
        }
    }
}
