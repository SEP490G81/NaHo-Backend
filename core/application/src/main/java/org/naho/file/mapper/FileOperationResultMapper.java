package org.naho.file.mapper;

import org.naho.file.model.FileOperation;
import org.naho.file.result.FileOperationResult;

public class FileOperationResultMapper {
    public FileOperationResult domainToResult(FileOperation domain) {
        if (domain == null) {
            return null;
        }

        return FileOperationResult.builder()
                .id(domain.getId())
                .fileId(domain.getFileId())
                .operationType(domain.getOperationType())
                .operationStatus(domain.getOperationStatus())
                .retryCount(domain.getRetryCount())
                .build();
    }
}
