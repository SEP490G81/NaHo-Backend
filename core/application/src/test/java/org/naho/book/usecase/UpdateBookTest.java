package org.naho.book.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.UpdateBookCommand;
import org.naho.book.exception.BookErrorCode;
import org.naho.book.model.Book;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.result.BookResult;
import org.naho.book.type.CefrLevel;
import org.naho.file.model.File;
import org.naho.file.port.in.AsyncCrudFileInputPort;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.type.JLPTLevel;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBookTest {

    @Mock
    private BookRepositoryPort bookRepositoryPort;
    @Mock
    private TransactionPort transactionPort;
    @Mock
    private CrudFileInputPort crudFileInputPort;
    @Mock
    private FileRepositoryPort fileRepositoryPort;
    @Mock
    private AsyncCrudFileInputPort asyncCrudFileInputPort;

    @InjectMocks
    private UpdateBookUseCase updateBookUseCase;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(transactionPort.execute(any(Supplier.class))).thenAnswer(inv -> {
            Supplier<?> s = inv.getArgument(0);
            return s.get();
        });
    }

    @Test
    @DisplayName("UTCID01 - Cập nhật Book thành công khi người dùng là Admin/Manager")
    void UTCID01_UpdateBook_Success() {
        UpdateBookCommand command = new UpdateBookCommand(1L, 20L, "New Title", "New Desc", JLPTLevel.N5, CefrLevel.A1, 1L, true);
        Book existingBook = Book.builder().id(1L).title("Old Title").jlptLevel(JLPTLevel.N5).cefrLevel(CefrLevel.A1).orderIndex(1.0).coverImageFileId(10L).build();
        File oldFile = mock(File.class);
        when(oldFile.getObjectKey()).thenReturn("old-key");

        when(bookRepositoryPort.findById(1L)).thenReturn(Optional.of(existingBook));
        when(fileRepositoryPort.findById(10L)).thenReturn(Optional.of(oldFile));

        BookResult result = updateBookUseCase.updateBook(command);

        assertNotNull(result);
        assertEquals("New Title", result.title());
        verify(bookRepositoryPort, times(1)).save(any(Book.class));
        verify(asyncCrudFileInputPort, times(1)).deleteFileInCloudAsync("old-key");
    }

    @Test
    @DisplayName("UTCID02 - Cập nhật Book thất bại do người dùng không có quyền")
    void UTCID02_UpdateBook_Forbidden() {
        UpdateBookCommand command = new UpdateBookCommand(1L, null, "Title", "Desc", JLPTLevel.N5, CefrLevel.A1, 1L, false);

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateBookUseCase.updateBook(command));
        assertEquals(BookErrorCode.BOOK_UPDATE_FORBIDDEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("UTCID03 - Cập nhật Book thất bại do không tìm thấy Book")
    void UTCID03_UpdateBook_NotFound() {
        UpdateBookCommand command = new UpdateBookCommand(99L, null, "Title", "Desc", JLPTLevel.N5, CefrLevel.A1, 1L, true);
        when(bookRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> updateBookUseCase.updateBook(command));
        assertEquals(BookErrorCode.BOOK_NOT_FOUND, ex.getErrorCode());
    }
}
