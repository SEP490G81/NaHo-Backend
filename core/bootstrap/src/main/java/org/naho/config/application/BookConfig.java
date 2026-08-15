package org.naho.config.application;

import org.naho.book.port.in.*;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.port.out.ImportBookPort;
import org.naho.book.usecase.*;
import org.naho.file.port.in.AsyncCrudFileInputPort;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookConfig {
    @Bean
    public ListBooksInputPort listBooksInputPort(
            BookRepositoryPort bookRepositoryPort,
            CrudFileInputPort crudFileInputPort
    ) {
        return new ListBooksUseCase(bookRepositoryPort, crudFileInputPort);
    }

    @Bean
    public GetBookDetailInputPort getBookDetailInputPort(
            BookRepositoryPort bookRepositoryPort,
            CrudFileInputPort crudFileInputPort
    ) {
        return new GetBookDetailUseCase(bookRepositoryPort, crudFileInputPort);
    }

    @Bean
    public ImportBookInputPort importBookInputPort(
            ImportBookPort importBookPort
    ) {
        return new ImportBookUseCase(importBookPort);
    }

    @Bean
    public UpdateBookInputPort updateBookInputPort(
            BookRepositoryPort bookRepositoryPort,
            TransactionPort transactionPort,
            CrudFileInputPort crudFileInputPort,
            FileRepositoryPort fileRepositoryPort,
            AsyncCrudFileInputPort asyncCrudFileInputPort
    ) {
        return new UpdateBookUseCase(
                bookRepositoryPort,
                transactionPort,
                crudFileInputPort,
                fileRepositoryPort,
                asyncCrudFileInputPort
        );
    }

    @Bean
    public UploadBookCoverImageInputPort uploadBookCoverImageInputPort(
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort
    ) {
        return new UploadBookCoverImageUseCase(
                fileRepositoryPort,
                uploadFileInputPort,
                transactionPort
        );
    }
}
