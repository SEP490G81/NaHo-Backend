package org.naho.book.port.in;

import org.naho.book.result.BookListItemResult;

import java.util.List;

public interface ListBooksInputPort {
    List<BookListItemResult> listBooks();
}
