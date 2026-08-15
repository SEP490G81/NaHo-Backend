package org.naho.book.port.in.in;

import org.naho.book.command.GetBookDetailCommand;
import org.naho.book.result.BookResult;

public interface GetBookDetailInputPort {
    BookResult getBookDetail(GetBookDetailCommand command);
}
