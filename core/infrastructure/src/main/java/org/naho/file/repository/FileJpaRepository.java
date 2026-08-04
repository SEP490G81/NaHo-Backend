package org.naho.file.repository;

import org.naho.file.entity.FileEntity;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FileJpaRepository extends BaseJpaRepository<FileEntity> {
    List<FileEntity> findAllByLeague_IdIn(Collection<Long> leagueIds);

    Optional<FileEntity> findByObjectKey(String objectKey);

    Optional<FileEntity> findByObjectKeyAndOperationType(String objectKey, OperationType operationType);

    Optional<FileEntity> findByIdAndOperationType(Long id, OperationType operationType);

    List<FileEntity> findTop20AllByOperationStatusAndOperationTypeAndRetryCountLessThanOrderByCreatedTime(OperationStatus operationStatus, OperationType operationType, Integer retryCount);
}
