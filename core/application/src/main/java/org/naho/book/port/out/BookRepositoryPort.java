package org.naho.book.port.out;

import org.naho.book.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepositoryPort {
    List<Book> findAllBooks();

    Optional<Book> findById(Long bookId);
}
