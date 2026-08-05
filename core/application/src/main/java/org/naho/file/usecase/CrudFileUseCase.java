package org.naho.file.usecase;

import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.util.List;

public class CrudFileUseCase implements CrudFileInputPort {

    private final FileRepositoryPort fileRepositoryPort;
    private final FileResultMapperPort fileResultMapperPort;

    public CrudFileUseCase(
            FileRepositoryPort fileRepositoryPort,
            FileResultMapperPort fileResultMapperPort) {
        this.fileRepositoryPort = fileRepositoryPort;
        this.fileResultMapperPort = fileResultMapperPort;
    }

    @Override
    public FileResult findById(Long id) {
        if (id == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL);
        }

        File file = fileRepositoryPort.findById(id);
        return fileResultMapperPort.domainToResult(file);
    }

    @Override
    public List<FileResult> findAllByLeagueIds(List<Long> leagueIds) {
        if (leagueIds == null || leagueIds.isEmpty())
            return List.of();
        List<File> files = fileRepositoryPort.findAllByLeagueIds(leagueIds);
        return files
                .stream()
                .map(fileResultMapperPort::domainToResult)
                .toList();
    }

    @Override
    public List<FileResult> findAllByBookIds(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return List.of();
        List<File> files = fileRepositoryPort.findAllByBookIds(ids);
        return files
                .stream()
                .map(fileResultMapperPort::domainToResult)
                .toList();
    }
}
