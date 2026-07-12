package org.naho.config.application;

import org.naho.file.mapper.FileResultMapper;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.usecase.CrudFileUseCase;
import org.naho.file.usecase.FileStorageUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {
    @Bean
    public FileResultMapper fileResultMapper() {
        return new FileResultMapper();
    }

    @Bean
    public FileStorageUseCase fileStorageUseCase(
            FileStorageServicePort fileStorageServicePort,
            FileRepositoryPort fileRepositoryPort,
            FileResultMapper fileResultMapper
    ) {
        return new FileStorageUseCase(
                fileStorageServicePort,
                fileRepositoryPort,
                fileResultMapper
        );
    }

    @Bean
    public CrudFileUseCase crudFileInputPort(
            FileRepositoryPort fileRepositoryPort,
            FileResultMapper fileResultMapper
    ) {
        return new CrudFileUseCase(
                fileRepositoryPort,
                fileResultMapper
        );
    }
}

