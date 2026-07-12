package org.naho.book.usecase;

import org.naho.book.port.in.ListBooksInputPort;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.model.Book;
import org.naho.book.result.BookListItemResult;

import java.util.List;

public class ListBooksUseCase implements ListBooksInputPort {
    private final BookRepositoryPort bookRepositoryPort;

    public ListBooksUseCase(BookRepositoryPort bookRepositoryPort) {
        this.bookRepositoryPort = bookRepositoryPort;
    }

    @Override
    public List<BookListItemResult> listBooks() {
        List<Book> books = bookRepositoryPort.findAllBooks();
        return books.stream()
                .map(book -> new BookListItemResult(
                        book.getId(),
                        book.getTitle(),
                        book.getDescription(),
                        book.getJlptLevel(),
                        book.getCefrLevel(),
                        book.getOrderIndex(),
                        book.getCoverImageFileId()
                ))
                .toList();
    }
}
