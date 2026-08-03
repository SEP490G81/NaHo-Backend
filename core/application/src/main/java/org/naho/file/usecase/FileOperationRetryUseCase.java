package org.naho.file.usecase;

import org.naho.file.command.UploadFileToCloudCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.model.StoredFile;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.in.FileOperationRetryInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.file.type.OperationStatus;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class FileOperationRetryUseCase implements FileOperationRetryInputPort {
    public static final int MAX_RETRY_COUNT = 3;

    private final CrudFileInputPort crudFileInputPort;
    private final FileRepositoryPort fileRepositoryPort;

    public FileOperationRetryUseCase(
            CrudFileInputPort crudFileInputPort,
            FileRepositoryPort fileRepositoryPort) {
        this.crudFileInputPort = crudFileInputPort;
        this.fileRepositoryPort = fileRepositoryPort;
    }

    @Override
    public FileResult retryUploadFileToCloud(Long id, boolean isPublic) {
        File file = fileRepositoryPort.findById(id);

        if (file.getRetryCount() != null && file.getRetryCount() >= MAX_RETRY_COUNT) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID,
                    id
            );
        }

        StoredFile storedFile = StoredFile.builder()
                .objectKey(file.getObjectKey())
                .originalFileName(file.getOriginalFileName())
                .contentType(file.getContentType())
                .size(file.getSize())
                .checksum(file.getChecksum())
                .build();

        FileResult uploadedFile = crudFileInputPort.uploadFileToCloud(
                new UploadFileToCloudCommand(storedFile, isPublic, true)
        );

        if (!OperationStatus.COMPLETED.equals(uploadedFile.getOperationStatus())) {
            file.incrementRetryCount();
        }

        return uploadedFile;
    }
}
