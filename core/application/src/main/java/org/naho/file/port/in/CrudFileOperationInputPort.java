package org.naho.file.port.in;

import org.naho.file.result.FileOperationResult;
import org.naho.file.type.OperationType;

public interface CrudFileOperationInputPort {
    FileOperationResult findByFileId(Long fileId);

    FileOperationResult findByFileIdAndOperationType(Long fileId, OperationType operationType);
}
