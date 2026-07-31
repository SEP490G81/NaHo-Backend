package org.naho.file.usecase;

import org.naho.file.command.SaveFileCommand;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileResultMapper;
import org.naho.file.model.File;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CrudFileUseCase implements CrudFileInputPort {

    private final FileRepositoryPort fileRepositoryPort;
    private final FileResultMapper fileResultMapper;

    public CrudFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileResultMapper fileResultMapper
    ) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileResultMapper = fileResultMapper;
    }

    @Override
    public FileResult findById(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_FOUND,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        File file = fileRepositoryPort.findById(id);
        return fileResultMapper.domainToResult(file);
    }

    @Override
    public List<FileResult> findAllByLeagueIds(List<Long> leagueIds) {
        List<File> files = fileRepositoryPort.findAllByLeagueIds(leagueIds);
        return files
                .stream()
                .map(fileResultMapper::domainToResult)
                .toList();
    }

    @Override
    public List<FileResult> findAllByBookIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<File> files = fileRepositoryPort.findAllByBookIds(ids);
        return files
                .stream()
                .map(fileResultMapper::domainToResult)
                .toList();
    }

    @Override
    public FileResult save(SaveFileCommand command) {
        if (command.folderName() == null || command.folderName().isBlank()) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_FOLDER_NAME_EMPTY
            );
        }

        String objectKey = command.folderName() +
                "/" +
                UUID.randomUUID() +
                "_" +
                Instant.now().toEpochMilli();

        File file = File.builder()
                .objectKey(objectKey)
                .originalName(command.originalName())
                .contentType(command.contentType())
                .size(command.size())
                .build();

        File savedFile = fileRepositoryPort.save(file);
        return fileResultMapper.domainToResult(savedFile);
    }
}
