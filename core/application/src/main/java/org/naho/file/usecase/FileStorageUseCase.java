package org.naho.file.usecase;

import org.naho.file.command.FileUploadCommand;
import org.naho.file.mapper.FileResultMapper;
import org.naho.file.model.File;
import org.naho.file.port.in.FileStorageInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;

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
    public FileResult upload(FileUploadCommand command) {
        String objectKey = UUID.randomUUID() + "-" + Instant.now().toEpochMilli();

        command.setOriginalName(objectKey);
        String previewKey = fileStorageServicePort.upload(command);

        File file = File.builder()
                .objectKey(objectKey)
                .previewKey(previewKey)
                .originalName(command.getOriginalName())
                .contentType(command.getContentType())
                .size(command.getSize())
                .build();

        File savedFile = fileRepositoryPort.save(file);
        return fileResultMapper.domainToResult(savedFile);
    }

    @Override
    public String deleteById(Long id) {
        return "";
    }

    @Override
    public String deleteByObjectKey(String objectKey) {
        return "";
    }
}
