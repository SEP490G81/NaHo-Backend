package org.naho.file.port.out;

import org.naho.file.command.UpdateOperationStatusCommand;
import org.naho.file.model.FileOperation;
import org.naho.file.type.OperationStatus;

public interface FileOperationRepositoryPort {
    FileOperation save(FileOperation domain);

    FileOperation updateOperationStatusById(UpdateOperationStatusCommand command);

    FileOperation updateOperationStatusByObjectKeyAndOperationType(UpdateOperationStatusCommand command);

    FileOperation updateOperationStatus(FileOperation fileOperation, OperationStatus operationStatus);
}
