package org.naho.config.application;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.usecase.CrudFileUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {
    @Bean
    public CrudFileInputPort crudFileInputPort(
            FileRepositoryPort fileRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileResultMapperPort fileResultMapperPort
    ) {
        return new CrudFileUseCase(
                fileRepositoryPort,
                fileStorageServicePort,
                fileResultMapperPort
        );
    }
}

