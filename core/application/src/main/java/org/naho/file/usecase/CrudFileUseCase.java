package org.naho.file.usecase;

import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileResultMapper;
import org.naho.file.model.File;
import org.naho.file.model.StoredFile;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.util.List;

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
    public FileResult save(StoredFile storedFile) {
        File file = File.builder()
                .localStoragePath(storedFile.localStoragePath())
                .objectKey(storedFile.objectKey())
                .originalFileName(storedFile.originalFileName())
                .contentType(storedFile.contentType())
                .size(storedFile.size())
                .build();
        File savedFile = fileRepositoryPort.save(file);
        return fileResultMapper.domainToResult(savedFile);
    }
}
