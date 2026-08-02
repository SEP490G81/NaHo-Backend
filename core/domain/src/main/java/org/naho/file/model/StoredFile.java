package org.naho.file.model;

public record StoredFile(
        String objectKey,
        String relativeLocalStoragePath,
        String absoluteLocalStoragePath,
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
        private String relativeLocalStoragePath;
        private String absoluteLocalStoragePath;
        private String originalFileName;
        private String contentType;
        private Long size;
        private String checksum;

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder relativeLocalStoragePath(String relativeLocalStoragePath) {
            this.relativeLocalStoragePath = relativeLocalStoragePath;
            return this;
        }

        public Builder absoluteLocalStoragePath(String absoluteLocalStoragePath) {
            this.absoluteLocalStoragePath = absoluteLocalStoragePath;
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
                    relativeLocalStoragePath,
                    absoluteLocalStoragePath,
                    originalFileName,
                    contentType,
                    size,
                    checksum
            );
        }
    }
}