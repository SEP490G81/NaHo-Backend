package org.naho.file.usecase;

import org.naho.file.mapper.FileResultMapper;
import org.naho.file.model.File;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;

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
}
