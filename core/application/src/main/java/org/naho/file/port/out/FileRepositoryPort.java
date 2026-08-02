package org.naho.file.port.out;

import org.naho.file.model.File;

import java.util.List;

public interface FileRepositoryPort {
    File findById(Long id);

    List<File> findAllByLeagueIds(List<Long> leagueIds);

    List<File> findAllByBookIds(List<Long> ids);

    File createNew(File file);

    File findByObjectKey(String objectKey);
}
