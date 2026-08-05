package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.command.UpdateOperationStatusCommand;
import org.naho.file.constant.FileProperties;
import org.naho.file.constant.S3Properties;
import org.naho.file.entity.FileEntity;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileEntityMapper;
import org.naho.file.model.File;
import org.naho.file.mybatis.FileQueryMapper;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.file.result.StoredFile;
import org.naho.file.type.OperationStatus;
import org.naho.file.type.OperationType;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepositoryPort {

    private final FileJpaRepository fileJpaRepository;
    private final FileEntityMapper fileEntityMapper;
    private final FileQueryMapper fileQueryMapper;
    private final S3Properties s3Properties;

    @Override
    public File findById(Long id) {
        if (id == null) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        FileEntity entity = fileJpaRepository
                .findById(id)
                .orElseThrow(() -> new InfrastructureException(
                        FileErrorCode.FILE_NOT_FOUND,
                        FileDetailMessageKey.FILE_NOT_FOUND,
                        id
                ));
        return fileEntityMapper.entityToDomain(entity);
    }

    @Override
    public File findByObjectKey(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY
            );
        }

        FileEntity entity = fileJpaRepository
                .findByObjectKey(objectKey)
                .orElseThrow(() -> new InfrastructureException(
                        FileErrorCode.FILE_NOT_FOUND,
                        FileDetailMessageKey.FILE_NOT_FOUND,
                        objectKey
                ));
        return fileEntityMapper.entityToDomain(entity);
    }

    @Override
    public List<File> findAllByLeagueIds(List<Long> leagueIds) {
        List<FileEntity> fileEntityList = fileJpaRepository.findAllByLeague_IdIn(leagueIds);
        return fileEntityList
                .stream()
                .map(fileEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<File> findAllByBookIds(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return List.of();
        List<FileEntity> fileEntityList = fileJpaRepository.findAllById(ids);
        return fileEntityList
                .stream()
                .map(fileEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public File createNewForUpload(StoredFile storedFile, boolean isPublic) {
        if (storedFile == null) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        File domain = File.builder()
                .objectKey(storedFile.objectKey())
                .originalFileName(storedFile.originalFileName())
                .contentType(storedFile.contentType())
                .size(storedFile.size())
                .checksum(storedFile.checksum())
                .operationType(OperationType.UPLOAD)
                .operationStatus(OperationStatus.PROCESSING)
                .retryCount(0)
                .nextRetryAt(null)
                .build();

        FileEntity entity = fileEntityMapper.domainToEntity(domain);

        String bucketName = isPublic ?
                s3Properties.getPublicBucketName() :
                s3Properties.getPrivateBucketName();

        entity.setBucketName(bucketName);

        FileEntity savedEntity = fileJpaRepository.save(entity);

        return fileEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public File updateOperationByObjectKeyAndOperationType(UpdateOperationStatusCommand command) {
        if (command == null || command.getObjectKey() == null || command.getObjectKey().isBlank()) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY
            );
        }

        FileEntity entity = fileJpaRepository
                .findByObjectKeyAndOperationType(command.getObjectKey(), command.getOperationType())
                .orElseGet(() -> fileJpaRepository.findByObjectKey(command.getObjectKey())
                        .orElseThrow(() -> new InfrastructureException(
                                FileErrorCode.FILE_NOT_FOUND,
                                FileDetailMessageKey.FILE_NOT_FOUND,
                                command.getObjectKey()
                        )));

        entity.setOperationStatus(command.getToOperationStatus());

        FileEntity savedEntity = fileJpaRepository.save(entity);
        return fileEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public File updateOperationStatus(File file, OperationStatus operationStatus) {
        if (file == null || file.getId() == null) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        FileEntity entity = fileJpaRepository
                .findById(file.getId())
                .orElseThrow(() -> new InfrastructureException(
                        FileErrorCode.FILE_NOT_FOUND,
                        FileDetailMessageKey.FILE_NOT_FOUND,
                        file.getId()
                ));

        entity.setOperationStatus(operationStatus);

        FileEntity savedEntity = fileJpaRepository.save(entity);
        return fileEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public File save(File file) {
        FileEntity entity = fileJpaRepository.findByObjectKey(file.getObjectKey())
                .orElseThrow(() -> new InfrastructureException(
                        FileErrorCode.FILE_NOT_FOUND,
                        FileDetailMessageKey.FILE_NOT_FOUND,
                        file.getId()
                ));

        entity.setObjectKey(file.getObjectKey());
        entity.setBucketName(file.getBucketName());
        entity.setOriginalFileName(file.getOriginalFileName());
        entity.setContentType(file.getContentType());
        entity.setSize(file.getSize());
        entity.setChecksum(file.getChecksum());

        entity.setOperationType(file.getOperationType());
        entity.setOperationStatus(file.getOperationStatus());
        entity.setRetryCount(file.getRetryCount());

        entity.setNextRetryAt(file.getNextRetryAt() != null ?
                file.getNextRetryAt().getValue() : null);

        FileEntity savedEntity = fileJpaRepository.save(entity);

        return fileEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public List<File> findAllForSchedulerRetryUpload(Instant now, OperationType operationType, OperationStatus operationStatus) {
        return fileQueryMapper.findAllForSchedulerRetryUpload(
                        now,
                        operationType,
                        operationStatus,
                        FileProperties.MAX_RETRY_COUNT
                ).stream().map(fileEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        fileJpaRepository.deleteById(id);
    }
}
