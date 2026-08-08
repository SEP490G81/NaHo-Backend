package org.naho.file.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.model.File;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.result.FileResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllByBookIdsTest {

    @Mock
    private FileRepositoryPort fileRepositoryPort;

    @Mock
    private FileResultMapperPort fileResultMapperPort;

    @InjectMocks
    private CrudFileUseCase crudFileUseCase;

    @Test
    @DisplayName("UTCID01 - Lấy danh sách file theo bookIds thành công khi ids hợp lệ và có dữ liệu")
    void UTCID01_FindAllByBookIdsSuccess() {
        // Arrange
        List<Long> bookIds = List.of(10L, 20L);
        File file1 = mock(File.class);
        File file2 = mock(File.class);
        FileResult result1 = mock(FileResult.class);
        FileResult result2 = mock(FileResult.class);

        when(fileRepositoryPort.findAllByBookIds(bookIds)).thenReturn(List.of(file1, file2));
        when(fileResultMapperPort.domainToResult(file1)).thenReturn(result1);
        when(fileResultMapperPort.domainToResult(file2)).thenReturn(result2);

        // Act
        List<FileResult> results = crudFileUseCase.findAllByBookIds(bookIds);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(result1, results.get(0));
        assertEquals(result2, results.get(1));
        verify(fileRepositoryPort, times(1)).findAllByBookIds(bookIds);
        verify(fileResultMapperPort, times(1)).domainToResult(file1);
        verify(fileResultMapperPort, times(1)).domainToResult(file2);
    }

    @Test
    @DisplayName("UTCID02 - Lấy danh sách file theo bookIds thất bại (trả về danh sách rỗng) khi ids bị null")
    void UTCID02_BookIdsNull() {
        // Arrange
        List<Long> bookIds = null;

        // Act
        List<FileResult> results = crudFileUseCase.findAllByBookIds(bookIds);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(fileRepositoryPort, never()).findAllByBookIds(any());
        verify(fileResultMapperPort, never()).domainToResult(any());
    }

    @Test
    @DisplayName("UTCID03 - Lấy danh sách file theo bookIds thất bại (trả về danh sách rỗng) khi ids rỗng")
    void UTCID03_BookIdsEmpty() {
        // Arrange
        List<Long> bookIds = Collections.emptyList();

        // Act
        List<FileResult> results = crudFileUseCase.findAllByBookIds(bookIds);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(fileRepositoryPort, never()).findAllByBookIds(any());
        verify(fileResultMapperPort, never()).domainToResult(any());
    }
}
