package org.naho.file.port.out;

import org.naho.file.command.UpdateOperationStatusCommand;
import org.naho.file.model.FileOperation;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;

import java.util.Optional;
import java.util.Set;

public interface FileOperationRepositoryPort {
    FileOperation save(FileOperation domain);

    FileOperation updateOperationStatusById(UpdateOperationStatusCommand command);

    FileOperation updateOperationStatusByObjectKeyAndOperationType(UpdateOperationStatusCommand command);

    FileOperation updateOperationStatus(FileOperation fileOperation, OperationStatus operationStatus);

    Optional<FileOperation> findByFileId(Long fileId);

    Optional<FileOperation> findByFileIdAndOperationType(Long fileId, OperationType operationType);
}
