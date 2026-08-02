package org.naho.file.repository;

import org.naho.file.entity.FileOperationEntity;
import org.naho.file.type.OperationType;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.Optional;

public interface FileOperationJpaRepository extends BaseJpaRepository<FileOperationEntity> {
    Optional<FileOperationEntity> findByFile_ObjectKeyAndOperationType(String fileObjectKey, OperationType operationType);
}
