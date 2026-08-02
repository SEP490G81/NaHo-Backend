package org.naho.file.result;

public record FileResult(
        Long id,
        String accessUrl,
        String originalFileName,
        String contentType,
        Long size,
        FileOperationResult fileOperation
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
            return new FileResult(
                    id,
                    accessUrl,
                    originalFileName,
                    contentType,
                    size,
                    fileOperation
            );
        }
    }
}
