package org.naho.book.port.in;

import org.naho.book.command.UpdateBookCommand;
import org.naho.book.result.BookResult;

public interface UpdateBookInputPort {
    BookResult updateBook(UpdateBookCommand command);
}
