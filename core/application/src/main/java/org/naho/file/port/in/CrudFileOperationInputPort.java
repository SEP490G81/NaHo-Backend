package org.naho.file.port.in;

import org.naho.file.result.FileOperationResult;
import org.naho.file.type.OperationType;

import java.util.Set;

public interface CrudFileOperationInputPort {
    Set<FileOperationResult> findAllByFileId(Long fileId);

    FileOperationResult findByFileIdAndOperationType(Long fileId, OperationType operationType);
}
