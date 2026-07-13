package org.naho.book.port.in;

import org.naho.book.result.BookResult;

import java.util.List;

public interface ListBooksInputPort {
    List<BookResult> listBooks();
}
