package org.naho.file.dto.response;

import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

public record FileResponse(
        Long id,
        String accessUrl,
        String originalFileName,
        String contentType,
        Long size,
        String checksum,
        OperationType operationType,
        OperationStatus operationStatus,
        Integer retryCount
) {
}
