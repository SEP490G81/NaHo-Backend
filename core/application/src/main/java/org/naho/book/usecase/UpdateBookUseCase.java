package org.naho.book.usecase;

import org.naho.book.command.UpdateBookCommand;
import org.naho.book.exception.BookErrorCode;
import org.naho.book.model.Book;
import org.naho.book.port.in.UpdateBookInputPort;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.result.BookResult;
import org.naho.file.model.File;
import org.naho.file.port.in.AsyncCrudFileInputPort;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.book.BookDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;

import java.util.Objects;

public class UpdateBookUseCase implements UpdateBookInputPort {

    private final BookRepositoryPort bookRepositoryPort;
    private final TransactionPort transactionPort;
    private final CrudFileInputPort crudFileInputPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final AsyncCrudFileInputPort asyncCrudFileInputPort;

    public UpdateBookUseCase(
            BookRepositoryPort bookRepositoryPort,
            TransactionPort transactionPort,
            CrudFileInputPort crudFileInputPort,
            FileRepositoryPort fileRepositoryPort,
            AsyncCrudFileInputPort asyncCrudFileInputPort
    ) {
        this.bookRepositoryPort = bookRepositoryPort;
        this.transactionPort = transactionPort;
        this.crudFileInputPort = crudFileInputPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.asyncCrudFileInputPort = asyncCrudFileInputPort;
    }

    @Override
    public BookResult updateBook(UpdateBookCommand command) {
        if (!command.isAdminOrManager()) {
            throw new ApplicationException(
                    BookErrorCode.BOOK_UPDATE_FORBIDDEN,
                    BookDetailMessageKey.BOOK_UPDATE_FORBIDDEN
            );
        }

        return transactionPort.execute(() -> {
            Book existingBook = bookRepositoryPort.findById(command.bookId())
                    .orElseThrow(() -> new ApplicationException(
                            BookErrorCode.BOOK_NOT_FOUND,
                            BookDetailMessageKey.BOOK_ID_NOT_FOUND
                    ));

            boolean isCoverImageChanged = !Objects.equals(existingBook.getCoverImageFileId(), command.coverImageFileId());
            File oldCoverImageFile = null;

            if (isCoverImageChanged && existingBook.getCoverImageFileId() != null) {
                oldCoverImageFile = fileRepositoryPort.findById(existingBook.getCoverImageFileId()).orElse(null);
                if (oldCoverImageFile != null) {
                    oldCoverImageFile.markDeleted();
                    oldCoverImageFile.markProcessing();
                    oldCoverImageFile.resetRetry();
                    fileRepositoryPort.save(oldCoverImageFile);
                }
            }

            Book updatedBook = Book.builder()
                    .id(existingBook.getId())
                    .title(command.title())
                    .description(command.description())
                    .coverImageFileId(command.coverImageFileId())
                    .jlptLevel(command.jlptLevel())
                    .cefrLevel(command.cefrLevel())
                    .orderIndex(existingBook.getOrderIndex())
                    .firstNodeGlobalOrderIndex(existingBook.getFirstNodeGlobalOrderIndex())
                    .lastNodeGlobalOrderIndex(existingBook.getLastNodeGlobalOrderIndex())
                    .build();

            bookRepositoryPort.save(updatedBook);

            if (oldCoverImageFile != null) {
                asyncCrudFileInputPort.deleteFileInCloudAsync(oldCoverImageFile.getObjectKey());
            }

            FileResult coverImage = updatedBook.getCoverImageFileId() != null
                    ? crudFileInputPort.findById(updatedBook.getCoverImageFileId())
                    : null;

            return new BookResult(
                    updatedBook.getId(),
                    updatedBook.getTitle(),
                    updatedBook.getDescription(),
                    updatedBook.getJlptLevel(),
                    updatedBook.getCefrLevel(),
                    updatedBook.getOrderIndex(),
                    updatedBook.getFirstNodeGlobalOrderIndex(),
                    updatedBook.getLastNodeGlobalOrderIndex(),
                    coverImage
            );
        });
    }
}
