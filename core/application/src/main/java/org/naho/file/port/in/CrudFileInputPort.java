package org.naho.file.port.in;

import org.naho.file.command.SaveFileCommand;
import org.naho.file.result.FileResult;

import java.util.List;

public interface CrudFileInputPort {
    FileResult findById(Long id);

    List<FileResult> findAllByLeagueIds(List<Long> leagueIds);

    List<FileResult> findAllByBookIds(List<Long> ids);

    FileResult save(SaveFileCommand command);
}
