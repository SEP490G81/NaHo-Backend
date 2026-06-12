package org.naho.file.usecase;

import org.naho.file.command.FileUploadCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileResultMapper;
import org.naho.file.model.File;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.time.Instant;
import java.util.UUID;

public class FileStorageUseCase implements FileStorageInputPort {
    private final FileStorageServicePort fileStorageServicePort;
    private final FileRepositoryPort fileRepositoryPort;
    private final FileResultMapper fileResultMapper;
    private final FileValidatorPort fileValidatorPort;

    public FileStorageUseCase(
            FileStorageServicePort fileStorageServicePort,
            FileRepositoryPort fileRepositoryPort,
            FileResultMapper fileResultMapper,
            FileValidatorPort fileValidatorPort
    ) {
        this.fileStorageServicePort = fileStorageServicePort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileResultMapper = fileResultMapper;
        this.fileValidatorPort = fileValidatorPort;
    }

    @Override
    public FileResult upload(FileUploadCommand command) {
        fileValidatorPort.validateFileUploadCommand(command);

        String objectKey = command.getFolderName() + "/" + UUID.randomUUID() + "-" + Instant.now().toEpochMilli();

        File file = File.builder()
                .objectKey(objectKey)
                .originalName(command.getOriginalName())
                .contentType(command.getContentType())
                .size(command.getSize())
                .build();

        File savedFile = fileRepositoryPort.save(file);

        fileStorageServicePort.upload(
                objectKey,
                command.getInputStream(),
                command.getContentType(),
                command.getSize()
        );

        return fileResultMapper.domainToResult(savedFile);
    }

    @Override
    public String deleteById(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_FOUND,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        String objectKey = fileRepositoryPort.findObjectKeyById(id);

        fileRepositoryPort.deleteById(id);

        fileStorageServicePort.delete(objectKey);
        return objectKey;
    }
}
