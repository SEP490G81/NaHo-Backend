package org.naho.book.port.out;

import org.naho.book.model.Book;

import java.util.List;

public interface BookRepositoryPort {
    List<Book> findAllBooks();
}
