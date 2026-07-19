package org.naho.book.usecase;

import org.naho.book.command.GetBookDetailCommand;
import org.naho.book.exception.BookErrorCode;
import org.naho.book.model.Book;
import org.naho.book.port.in.GetBookDetailInputPort;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.result.BookResult;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.book.BookDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

public class GetBookDetailUseCase implements GetBookDetailInputPort {

    private final BookRepositoryPort bookRepositoryPort;
    private final CrudFileInputPort crudFileInputPort;

    public GetBookDetailUseCase(BookRepositoryPort bookRepositoryPort, CrudFileInputPort crudFileInputPort) {
        this.bookRepositoryPort = bookRepositoryPort;
        this.crudFileInputPort = crudFileInputPort;
    }

    @Override
    public BookResult getBookDetail(GetBookDetailCommand command) {
        Book book = bookRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException(
                        BookErrorCode.BOOK_NOT_FOUND,
                        BookDetailMessageKey.BOOK_ID_NOT_FOUND,
                        command.id()));

        FileResult coverImage = book.getCoverImageFileId() != null
                ? crudFileInputPort.findById(book.getCoverImageFileId())
                : null;

        return new BookResult(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getJlptLevel(),
                book.getCefrLevel(),
                book.getOrderIndex(),
                book.getFirstNodeGlobalOrderIndex(),
                book.getLastNodeGlobalOrderIndex(),
                coverImage
        );
    }
}
