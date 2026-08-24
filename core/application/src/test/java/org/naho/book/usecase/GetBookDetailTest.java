package org.naho.book.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.command.GetBookDetailCommand;
import org.naho.book.exception.BookErrorCode;
import org.naho.book.model.Book;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.result.BookResult;
import org.naho.book.type.CefrLevel;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.type.JLPTLevel;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBookDetailTest {

    @Mock
    private BookRepositoryPort bookRepositoryPort;

    @Mock
    private CrudFileInputPort crudFileInputPort;

    @InjectMocks
    private GetBookDetailUseCase getBookDetailUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy chi tiết sách thành công khi tồn tại Book và có cover image")
    void UTCID01_GetBookDetail_Success() {
        GetBookDetailCommand command = new GetBookDetailCommand(1L);
        Book book = Book.builder()
                .id(1L)
                .title("Minna no Nihongo")
                .description("Book description")
                .jlptLevel(JLPTLevel.N5)
                .cefrLevel(CefrLevel.A1)
                .orderIndex(1.0)
                .coverImageFileId(10L)
                .build();

        FileResult fileResult = FileResult.builder().id(10L).accessUrl("http://img.url").build();

        when(bookRepositoryPort.findById(1L)).thenReturn(Optional.of(book));
        when(crudFileInputPort.findById(10L)).thenReturn(fileResult);

        BookResult result = getBookDetailUseCase.getBookDetail(command);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Minna no Nihongo", result.title());
        assertNotNull(result.coverImage());
        verify(bookRepositoryPort, times(1)).findById(1L);
    }

    @Test
    @DisplayName("UTCID02 - Lấy chi tiết sách thất bại khi không tìm thấy Book")
    void UTCID02_GetBookDetail_NotFound() {
        GetBookDetailCommand command = new GetBookDetailCommand(99L);
        when(bookRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        ApplicationException ex = assertThrows(ApplicationException.class, () -> getBookDetailUseCase.getBookDetail(command));
        assertEquals(BookErrorCode.BOOK_NOT_FOUND, ex.getErrorCode());
    }
}
