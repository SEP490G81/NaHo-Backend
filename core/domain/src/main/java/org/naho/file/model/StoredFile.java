package org.naho.file.model;

public record StoredFile(
        String objectKey,
        String localStoragePath,
        String originalFileName,
        String contentType,
        Long size,
        String checksum
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String objectKey;
        private String localStoragePath;
        private String originalFileName;
        private String contentType;
        private Long size;
        private String checksum;

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder localStoragePath(String localStoragePath) {
            this.localStoragePath = localStoragePath;
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
            return new StoredFile(
                    objectKey,
                    localStoragePath,
                    originalFileName,
                    contentType,
                    size,
                    checksum
            );
        }
    }
}