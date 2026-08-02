package org.naho.config.application;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileOperationRepositoryPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.usecase.CrudFileUseCase;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {
    @Bean
    public CrudFileInputPort crudFileInputPort(
            FileRepositoryPort fileRepositoryPort,
            FileOperationRepositoryPort fileOperationRepositoryPort,
            FileStorageServicePort fileStorageServicePort,
            FileResultMapperPort fileResultMapperPort,
            TransactionPort transactionPort
    ) {
        return new CrudFileUseCase(
                fileRepositoryPort,
                fileOperationRepositoryPort,
                fileStorageServicePort,
                fileResultMapperPort,
                transactionPort
        );
    }
}

