package org.naho.file.repository;

import org.naho.file.entity.FileEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.Collection;
import java.util.List;

public interface FileJpaRepository extends BaseJpaRepository<FileEntity> {
    List<FileEntity> findAllByLeague_IdIn(Collection<Long> leagueIds);
}
