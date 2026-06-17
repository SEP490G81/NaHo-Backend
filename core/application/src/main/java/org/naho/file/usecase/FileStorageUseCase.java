package org.naho.file.usecase;

import org.naho.file.command.FileUploadCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileResultMapper;
import org.naho.file.model.File;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.time.Instant;
import java.util.UUID;

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
    public FileResult uploadFile(FileUploadCommand command) {
        if (command == null || command.getInputStream() == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_EMPTY
            );
        }

        if (command.getFolderName() == null || command.getFolderName().isBlank()) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_FOLDER_NAME_EMPTY
            );
        }

        String objectKey = command.getFolderName() + "/" + UUID.randomUUID() + "-" + Instant.now().toEpochMilli();

        File file = File.builder()
                .objectKey(objectKey)
                .originalName(command.getOriginalName())
                .contentType(command.getContentType())
                .size(command.getSize())
                .build();

        File savedFile = fileRepositoryPort.save(file);

        fileStorageServicePort.uploadFile(
                objectKey,
                command.getInputStream(),
                command.getContentType(),
                command.getSize()
        );

        return fileResultMapper.domainToResult(savedFile);
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

