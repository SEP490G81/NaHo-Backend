package org.naho.file.mapper;

import org.naho.file.model.File;
import org.naho.file.result.StoredFile;

public class StoredFileMapper {
    public StoredFile domainToStoredFile(File file) {
        if (file == null) {
            return null;
        }

        return StoredFile.builder()
                .objectKey(file.getObjectKey())
                .bucketName(file.getBucketName())
                .originalFileName(file.getOriginalFileName())
                .contentType(file.getContentType())
                .size(file.getSize())
                .checksum(file.getChecksum())
                .build();
    }

    public File storedFileToDomain(StoredFile storedFile) {
        if (storedFile == null) {
            return null;
        }

        return File.builder()
                .objectKey(storedFile.objectKey())
                .bucketName(storedFile.bucketName())
                .originalFileName(storedFile.originalFileName())
                .contentType(storedFile.contentType())
                .size(storedFile.size())
                .checksum(storedFile.checksum())
                .build();
    }
}
