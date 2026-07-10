package org.naho.config.application;

import org.naho.book.adapter.BookRepositoryAdapter;
import org.naho.book.port.in.ListBooksInputPort;
import org.naho.book.usecase.ListBooksUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookConfig {
    @Bean
    public ListBooksInputPort listBooksInputPort(BookRepositoryAdapter bookRepositoryAdapter) {
        return new ListBooksUseCase(bookRepositoryAdapter);
    }
}
