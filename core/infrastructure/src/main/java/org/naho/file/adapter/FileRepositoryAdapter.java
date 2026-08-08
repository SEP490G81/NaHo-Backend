package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
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
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.user.exception.UserErrorCode;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepositoryPort {

    private final FileJpaRepository fileJpaRepository;
    private final FileEntityMapper fileEntityMapper;
    private final FileQueryMapper fileQueryMapper;
    private final S3Properties s3Properties;

    @Override
    public Optional<File> findById(Long id) {
        if (id == null) {
            throw new InfrastructureException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        return fileJpaRepository
                .findById(id)
                .map(fileEntityMapper::entityToDomain);
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
                .commentId(storedFile.commentId())
                .reportId(storedFile.reportId())
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
    public List<File> findAllForSchedulerRetry(Instant now, OperationType operationType, OperationStatus operationStatus) {
        return fileQueryMapper.findAllForSchedulerRetry(
                        now,
                        operationType,
                        operationStatus,
                        FileProperties.MAX_RETRY_COUNT
                ).stream().map(fileEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<File> findAllForSchedulerRetryDelete(Instant now, OperationType operationType) {
        return fileQueryMapper.findAllForSchedulerRetryDelete(now, operationType)
                .stream().map(fileEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        fileJpaRepository.deleteById(id);
    }

    @Override
    public List<File> findAllByReportId(Long reportId) {
        if (reportId == null) {
            return List.of();
        }
        List<FileEntity> entities = fileJpaRepository.findAllByReport_Id(reportId);
        return entities.stream()
                .map(fileEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<File> findAvatarFileByUserId(Long userId) {
        if (userId == null) {
            throw new InfrastructureException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        return fileQueryMapper
                .findAvatarFileByUserId(userId)
                .map(fileEntityMapper::entityToDomain);
    }
}
