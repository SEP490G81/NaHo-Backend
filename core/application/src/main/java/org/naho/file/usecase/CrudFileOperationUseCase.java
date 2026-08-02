package org.naho.file.usecase;

import org.naho.file.exception.FileErrorCode;
import org.naho.file.exception.FileOperationErrorCode;
import org.naho.file.mapper.FileOperationResultMapper;
import org.naho.file.port.in.CrudFileOperationInputPort;
import org.naho.file.port.out.FileOperationRepositoryPort;
import org.naho.file.result.FileOperationResult;
import org.naho.file.type.OperationType;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.file.FileOperationDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.util.Set;
import java.util.stream.Collectors;

public class CrudFileOperationUseCase implements CrudFileOperationInputPort {
    private final FileOperationRepositoryPort fileOperationRepositoryPort;
    private final FileOperationResultMapper fileOperationResultMapper;

    public CrudFileOperationUseCase(
            FileOperationRepositoryPort fileOperationRepositoryPort,
            FileOperationResultMapper fileOperationResultMapper
    ) {
        this.fileOperationRepositoryPort = fileOperationRepositoryPort;
        this.fileOperationResultMapper = fileOperationResultMapper;
    }

    @Override
    public FileOperationResult findByFileId(Long fileId) {
        if (fileId == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        return fileOperationRepositoryPort.findByFileId(fileId)
                .map(fileOperationResultMapper::domainToResult)
                .orElse(null);
    }

    @Override
    public FileOperationResult findByFileIdAndOperationType(Long fileId, OperationType operationType) {
        if (fileId == null) {
            throw new ApplicationException(
                    FileOperationErrorCode.FILE_OPERATION_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        if (operationType == null) {
            throw new ApplicationException(
                    FileOperationErrorCode.FILE_OPERATION_NOT_VALID,
                    FileDetailMessageKey.FILE_OPERATION_TYPE_EMPTY
            );
        }

        return fileOperationRepositoryPort
                .findByFileIdAndOperationType(fileId, operationType)
                .map(fileOperationResultMapper::domainToResult)
                .orElseThrow(() -> new ApplicationException(
                        FileOperationErrorCode.FILE_OPERATION_NOT_FOUND,
                        FileOperationDetailMessageKey.FILE_OPERATION_NOT_FOUND
                ));
    }
}
