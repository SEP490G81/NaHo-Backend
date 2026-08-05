package org.naho.file.command;

import org.naho.file.result.StoredFile;

public record UploadFileToCloudCommand(
        StoredFile storedFile,
        boolean isPublic
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private StoredFile storedFile;
        private boolean isPublic;

        public Builder storedFile(StoredFile storedFile) {
            this.storedFile = storedFile;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public UploadFileToCloudCommand build() {
            return new UploadFileToCloudCommand(storedFile, isPublic);
        }
    }
}
