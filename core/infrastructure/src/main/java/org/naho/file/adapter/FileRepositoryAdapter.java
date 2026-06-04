package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileEntityMapper;
import org.naho.file.model.File;
import org.naho.file.model.FileEntity;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepositoryPort {

    private final FileJpaRepository fileJpaRepository;
    private final FileEntityMapper fileEntityMapper;

    @Override
    public String findObjectKeyById(Long id) {
        if (id == null) return null;
        FileEntity file = fileJpaRepository.findById(id)
                .orElseThrow(() -> new InfrastructureException(
                        FileErrorCode.FILE_NOT_FOUND,
                        FileDetailMessageKey.FILE_NOT_FOUND,
                        id
                ));
        return file.getObjectKey();
    }

    @Override
    public File save(File file) {
        if (file == null) return null;
        FileEntity fileEntity = fileEntityMapper.domainToEntity(file);
        FileEntity savedFileEntity = fileJpaRepository.save(fileEntity);
        return fileEntityMapper.entityToDomain(savedFileEntity);
    }

    @Override
    public void deleteById(Long id) {
        fileJpaRepository.deleteById(id);
    }
}
