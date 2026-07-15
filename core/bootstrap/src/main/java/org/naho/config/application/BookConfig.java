package org.naho.config.application;

import org.naho.book.port.in.GetBookDetailInputPort;
import org.naho.book.port.in.ListBooksInputPort;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.usecase.GetBookDetailUseCase;
import org.naho.book.usecase.ListBooksUseCase;
import org.naho.file.port.in.CrudFileInputPort;
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
}
