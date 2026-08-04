package org.naho.config.application;

import org.naho.file.mapper.StoredFileMapper;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.in.RetryUploadFileInputPort;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.usecase.CrudFileUseCase;
import org.naho.file.usecase.RetryUploadFileUseCase;
import org.naho.file.usecase.UploadFileUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

    @Bean
    public StoredFileMapper storedFileMapper() {
        return new StoredFileMapper();
    }

    @Bean
    public CrudFileInputPort crudFileInputPort(
            FileRepositoryPort fileRepositoryPort,
            FileResultMapperPort fileResultMapperPort
    ) {
        return new CrudFileUseCase(
                fileRepositoryPort,
                fileResultMapperPort
        );
    }

    @Bean
    public UploadFileInputPort uploadFileInputPort(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileResultMapperPort fileResultMapperPort
    ) {
        return new UploadFileUseCase(
                fileRepositoryPort,
                fileStorageServicePort,
                fileResultMapperPort
        );
    }

    @Bean
    public RetryUploadFileInputPort retryUploadFileInputPort(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            StoredFileMapper storedFileMapper
    ) {
        return new RetryUploadFileUseCase(
                fileRepositoryPort,
                fileStorageServicePort,
                storedFileMapper
        );
    }
}

