package org.naho.file.mapper;

import org.naho.file.model.FileOperation;
import org.naho.file.result.FileOperationResult;

public class FileOperationResultMapper {
    public FileOperationResult domainToResult(FileOperation domain) {
        if (domain == null) {
            return null;
        }

        return FileOperationResult.builder()
                .operationType(domain.getOperationType())
                .operationStatus(domain.getOperationStatus())
                .build();
    }
}
