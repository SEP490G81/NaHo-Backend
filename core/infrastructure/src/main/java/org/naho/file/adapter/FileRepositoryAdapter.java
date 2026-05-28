package org.naho.file.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.FileApplicationMessageKey;
import org.naho.file.exception.FileApplicationErrorCode;
import org.naho.file.model.FileEntity;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepositoryPort {

    private final FileJpaRepository fileJpaRepository;

    @Override
    public String findFileUrlById(Long id) {
        if (id == null) return null;
        FileEntity file = fileJpaRepository.findById(id)
                .orElseThrow(() -> new InfrastructureException(
                        FileApplicationErrorCode.FILE_NOT_FOUND,
                        FileApplicationMessageKey.FILE_NOT_FOUND,
                        id
                ));
        return file.getFileUrl();
    }
}
