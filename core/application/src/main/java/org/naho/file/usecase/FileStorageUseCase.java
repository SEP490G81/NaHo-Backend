package org.naho.file.usecase;

import org.naho.file.command.UploadFileCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileResultMapper;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class FileStorageUseCase implements FileStorageInputPort {
    private final FileStorageServicePort fileStorageServicePort;
    private final FileRepositoryPort fileRepositoryPort;
    private final FileResultMapper fileResultMapper;

    public FileStorageUseCase(
            FileStorageServicePort fileStorageServicePort,
            FileRepositoryPort fileRepositoryPort,
            FileResultMapper fileResultMapper
    ) {
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileResultMapper = fileResultMapper;
    }

    @Override
    public void uploadFile(UploadFileCommand command) {
        if (command == null || command.getInputStream() == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_EMPTY
            );
        }

        if (command.getObjectKey() == null || command.getObjectKey().isBlank()) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY
            );
        }

        fileStorageServicePort.uploadFile(
                command.getObjectKey(),
                command.getInputStream(),
                command.getContentType(),
                command.getSize()
        );
    }

    @Override
    public String deleteFileById(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_FOUND,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        String objectKey = fileRepositoryPort.findObjectKeyById(id);

        fileRepositoryPort.deleteById(id);

        fileStorageServicePort.deleteFileByObjectKey(objectKey);
        return objectKey;
    }
}
