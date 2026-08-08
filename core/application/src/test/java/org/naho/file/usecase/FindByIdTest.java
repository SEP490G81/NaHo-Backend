package org.naho.file.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindByIdTest {

    @Mock
    private FileRepositoryPort fileRepositoryPort;

    @Mock
    private FileResultMapperPort fileResultMapperPort;

    @InjectMocks
    private CrudFileUseCase crudFileUseCase;

    @Test
    @DisplayName("UTCID01 - Tìm file thành công theo ID khi id hợp lệ và file tồn tại")
    void UTCID01_FindByIdSuccess() {
        // Arrange
        Long fileId = 1L;
        File file = mock(File.class);
        FileResult expectedResult = mock(FileResult.class);

        when(fileRepositoryPort.findById(fileId)).thenReturn(Optional.of(file));
        when(fileResultMapperPort.domainToResult(file)).thenReturn(expectedResult);

        // Act
        FileResult result = crudFileUseCase.findById(fileId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(fileRepositoryPort, times(1)).findById(fileId);
        verify(fileResultMapperPort, times(1)).domainToResult(file);
    }

    @Test
    @DisplayName("UTCID02 - Tìm file thất bại khi id bị null")
    void UTCID02_FileIdNull() {
        // Arrange
        Long fileId = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudFileUseCase.findById(fileId)
        );

        assertEquals(FileErrorCode.FILE_NOT_VALID, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_ID_NULL, exception.getMessage());
        verify(fileRepositoryPort, never()).findById(any());
        verify(fileResultMapperPort, never()).domainToResult(any());
    }

    @Test
    @DisplayName("UTCID03 - Tìm file thất bại khi không tìm thấy file với id tương ứng")
    void UTCID03_FileNotFound() {
        // Arrange
        Long fileId = 99L;

        when(fileRepositoryPort.findById(fileId)).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> crudFileUseCase.findById(fileId)
        );

        assertEquals(FileErrorCode.FILE_NOT_FOUND, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_NOT_FOUND, exception.getMessage());
        verify(fileRepositoryPort, times(1)).findById(fileId);
        verify(fileResultMapperPort, never()).domainToResult(any());
    }
}
