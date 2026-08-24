package org.naho.book.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.book.model.Book;
import org.naho.book.port.out.BookRepositoryPort;
import org.naho.book.result.BookResult;
import org.naho.book.type.CefrLevel;
import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.user.type.JLPTLevel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListBooksTest {

    @Mock
    private BookRepositoryPort bookRepositoryPort;
    @Mock
    private CrudFileInputPort crudFileInputPort;

    @InjectMocks
    private ListBooksUseCase listBooksUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách tất cả Books thành công")
    void UTCID01_ListBooks_Success() {
        Book book = Book.builder()
                .id(1L)
                .title("Minna 1")
                .jlptLevel(JLPTLevel.N5)
                .cefrLevel(CefrLevel.A1)
                .orderIndex(1.0)
                .coverImageFileId(10L)
                .build();
        FileResult fileResult = FileResult.builder().id(10L).accessUrl("url").build();

        when(bookRepositoryPort.findAllBooks()).thenReturn(List.of(book));
        when(crudFileInputPort.findAllByBookIds(anyList())).thenReturn(List.of(fileResult));

        List<BookResult> result = listBooksUseCase.listBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Minna 1", result.get(0).title());
        assertNotNull(result.get(0).coverImage());
    }
}
