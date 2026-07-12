package org.naho.book.usecase;

import org.naho.book.port.in.ListBooksInputPort;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.model.Book;
import org.naho.book.result.BookListItemResult;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListBooksUseCase implements ListBooksInputPort {
    private final BookRepositoryPort bookRepositoryPort;
    private final CrudFileInputPort crudFileInputPort;

    public ListBooksUseCase(BookRepositoryPort bookRepositoryPort, CrudFileInputPort crudFileInputPort) {
        this.bookRepositoryPort = bookRepositoryPort;
        this.crudFileInputPort = crudFileInputPort;
    }

    @Override
    public List<BookListItemResult> listBooks() {
        List<Book> books = bookRepositoryPort.findAllBooks();

        List<Long> coverImageFileIds = books.stream()
                .map(Book::getCoverImageFileId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<FileResult> fileResults = crudFileInputPort.findAllByBookIds(coverImageFileIds);

        // create map
        // key: id (file result id) and value: file result
        Map<Long, FileResult> fileResultMap = fileResults.stream()
                .collect(Collectors.toMap(
                        FileResult::id,
                        Function.identity()
                ));

        return books.stream()
                .map(book -> new BookListItemResult(
                        book.getId(),
                        book.getTitle(),
                        book.getDescription(),
                        book.getJlptLevel(),
                        book.getCefrLevel(),
                        book.getOrderIndex(),
                        book.getCoverImageFileId() != null ? fileResultMap.get(book.getCoverImageFileId()) : null
                ))
                .toList();
    }
}
