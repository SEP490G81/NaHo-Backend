package org.naho.file.result;

import java.util.List;

public record FileResult(
        Long id,
        String accessUrl,
        String originalFileName,
        String contentType,
        Long size,
        List<FileOperationResult> fileOperations
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String accessUrl;
        private String originalFileName;
        private String contentType;
        private Long size;
        private List<FileOperationResult> fileOperations;

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

        public Builder fileOperations(List<FileOperationResult> fileOperations) {
            this.fileOperations = fileOperations;
            return this;
        }

        public FileResult build() {
            return new FileResult(
                    id,
                    accessUrl,
                    originalFileName,
                    contentType,
                    size,
                    fileOperations
            );
        }
    }
}
