package org.naho.file.result;

import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

public record FileOperationResult(
        Long id,
        Long fileId,
        OperationType operationType,
        OperationStatus operationStatus,
        Integer retryCount
) {
}
