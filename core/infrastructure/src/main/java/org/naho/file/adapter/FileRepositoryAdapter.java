package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.entity.FileEntity;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileEntityMapper;
import org.naho.file.model.File;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepositoryPort {

    private final FileJpaRepository fileJpaRepository;
    private final FileEntityMapper fileEntityMapper;

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
    public File createNew(File file) {
        FileEntity entity = fileEntityMapper.domainToEntity(file);
        FileEntity savedEntity = fileJpaRepository.save(entity);
        return fileEntityMapper.entityToDomain(savedEntity);
    }
}
