package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.command.UpdateOperationStatusCommand;
import org.naho.file.entity.FileOperationEntity;
import org.naho.file.exception.FileOperationErrorCode;
import org.naho.file.mapper.FileOperationEntityMapper;
import org.naho.file.model.FileOperation;
import org.naho.file.port.out.FileOperationRepositoryPort;
import org.naho.file.repository.FileOperationJpaRepository;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.file.FileOperationDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FileOperationRepositoryAdapter implements FileOperationRepositoryPort {

    private final FileOperationEntityMapper fileOperationEntityMapper;
    private final FileOperationJpaRepository fileOperationJpaRepository;

    @Override
    public FileOperation save(FileOperation domain) {
        if (domain == null) {
            throw new InfrastructureException(
                    FileOperationErrorCode.FILE_OPERATION_NOT_VALID,
                    FileOperationDetailMessageKey.FILE_OPERATION_NOT_VALID
            );
        }
        FileOperationEntity entity = fileOperationEntityMapper.domainToEntity(domain);
        FileOperationEntity savedEntity = fileOperationJpaRepository.save(entity);
        return fileOperationEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public FileOperation updateOperationStatusById(UpdateOperationStatusCommand command) {
        if (command == null || command.fileOperationId() == null) {
            throw new InfrastructureException(
                    FileOperationErrorCode.FILE_OPERATION_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }
        FileOperationEntity entity = fileOperationJpaRepository
                .findById(command.fileOperationId())
                .orElseThrow(() -> new InfrastructureException(
                        FileOperationErrorCode.FILE_OPERATION_NOT_FOUND,
                        FileOperationDetailMessageKey.FILE_OPERATION_NOT_FOUND,
                        command.fileOperationId()
                ));

        entity.setOperationStatus(command.toOperationStatus());
        FileOperationEntity savedEntity = fileOperationJpaRepository.save(entity);
        return fileOperationEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public FileOperation updateOperationStatusByObjectKeyAndOperationType(UpdateOperationStatusCommand command) {
        if (command == null || command.objectKey() == null || command.objectKey().isBlank()) {
            throw new InfrastructureException(
                    FileOperationErrorCode.FILE_OPERATION_NOT_VALID,
                    FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY
            );
        }
        if (command.operationType() == null) {
            throw new InfrastructureException(
                    FileOperationErrorCode.FILE_OPERATION_NOT_VALID,
                    FileDetailMessageKey.FILE_OPERATION_TYPE_EMPTY
            );
        }

        FileOperationEntity entity = fileOperationJpaRepository
                .findByFile_ObjectKeyAndOperationType(
                        command.objectKey(),
                        command.operationType()
                )
                .orElseThrow(() -> new InfrastructureException(
                        FileOperationErrorCode.FILE_OPERATION_NOT_FOUND,
                        FileOperationDetailMessageKey.FILE_OPERATION_NOT_FOUND,
                        command.objectKey()
                ));

        entity.setOperationStatus(command.toOperationStatus());
        FileOperationEntity savedEntity = fileOperationJpaRepository.save(entity);
        return fileOperationEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public FileOperation updateOperationStatus(FileOperation fileOperation, OperationStatus operationStatus) {
        if (fileOperation == null) {
            throw new InfrastructureException(
                    FileOperationErrorCode.FILE_OPERATION_NOT_VALID,
                    FileOperationDetailMessageKey.FILE_OPERATION_NOT_VALID
            );
        }

        if (operationStatus == null) {
            throw new InfrastructureException(
                    FileOperationErrorCode.FILE_OPERATION_NOT_VALID,
                    FileDetailMessageKey.FILE_OPERATION_STATUS_EMPTY
            );
        }

        FileOperationEntity entity = fileOperationEntityMapper.domainToEntity(fileOperation);
        entity.setOperationStatus(operationStatus);

        FileOperationEntity savedEntity = fileOperationJpaRepository.save(entity);
        return fileOperationEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<FileOperation> findByFileId(Long fileId) {
        return fileOperationJpaRepository.findByFile_Id(fileId)
                .map(fileOperationEntityMapper::entityToDomain);
    }

    @Override
    public Optional<FileOperation> findByFileIdAndOperationType(Long fileId, OperationType operationType) {
        return fileOperationJpaRepository
                .findByFile_IdAndOperationType(fileId, operationType)
                .map(fileOperationEntityMapper::entityToDomain);
    }
}
