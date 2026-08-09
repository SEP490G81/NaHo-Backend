package org.naho.config.application;

import org.naho.file.mapper.StoredFileMapper;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.in.DeleteFileInputPort;
import org.naho.file.port.in.DownloadFileInputPort;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.usecase.CrudFileUseCase;
import org.naho.file.usecase.DeleteFileUseCase;
import org.naho.file.usecase.DownloadFileUseCase;
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
            FileResultMapperPort fileResultMapperPort,
            StoredFileMapper storedFileMapper
    ) {
        return new UploadFileUseCase(
                fileRepositoryPort,
                fileStorageServicePort,
                fileResultMapperPort,
                storedFileMapper
        );
    }

    @Bean
    public DownloadFileInputPort downloadFileInputPort(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort
    ) {
        return new DownloadFileUseCase(
                fileRepositoryPort,
                fileStorageServicePort
        );
    }

    @Bean
    public DeleteFileInputPort deleteFileInputPort(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort
    ) {
        return new DeleteFileUseCase(
                fileRepositoryPort,
                fileStorageServicePort
        );
    }
}

