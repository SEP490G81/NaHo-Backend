package org.naho.file.port.out;

import org.naho.file.model.File;

import java.util.List;

public interface FileRepositoryPort {
    String findObjectKeyById(Long id);

    File save(File file);

    void deleteById(Long id);

    File findById(Long id);

    List<File> findAllByLeagueIds(List<Long> leagueIds);

    List<File> findAllByBookIds(List<Long> ids);
}
