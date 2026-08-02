package org.naho.file.dto.response;

import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

public record FileOperationResponse(
        OperationType operationType,
        OperationStatus operationStatus
) {
}
