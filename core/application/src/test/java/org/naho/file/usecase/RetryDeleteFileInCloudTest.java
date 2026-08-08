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
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetryDeleteFileInCloudTest {

    @Mock
    private FileRepositoryPort fileRepositoryPort;

    @Mock
    private FileStorageServicePort fileStorageServicePort;

    @InjectMocks
    private DeleteFileUseCase deleteFileUseCase;

    @Test
    @DisplayName("UTCID01 - Thử lại xóa file trên cloud thành công")
    void UTCID01_RetryDeleteFileInCloudSuccess() {
        // Arrange
        Long fileId = 5L;
        File file = mock(File.class);
        when(file.getId()).thenReturn(fileId);
        when(file.incrementRetryCount()).thenReturn(1);

        // Act
        deleteFileUseCase.retryDeleteFileInCloud(file);

        // Assert
        verify(file, times(1)).incrementRetryCount();
        verify(fileStorageServicePort, times(1)).deleteFileInCloud(file);
        verify(fileRepositoryPort, times(1)).deleteById(fileId);
    }

    @Test
    @DisplayName("UTCID02 - Thử lại xóa file trên cloud thất bại khi file bị null")
    void UTCID02_FileNull() {
        // Arrange
        File file = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> deleteFileUseCase.retryDeleteFileInCloud(file)
        );

        assertEquals(FileErrorCode.FILE_NOT_VALID, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_NOT_VALID, exception.getMessage());
        verify(fileStorageServicePort, never()).deleteFileInCloud(any());
        verify(fileRepositoryPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("UTCID03 - Thử lại xóa file trên cloud gặp lỗi ngoại lệ, cập nhật thời điểm retry tiếp theo")
    void UTCID03_CloudStorageError() {
        // Arrange
        File file = mock(File.class);
        when(file.incrementRetryCount()).thenReturn(2);
        doThrow(new RuntimeException("Storage error"))
                .when(fileStorageServicePort).deleteFileInCloud(file);

        // Act
        deleteFileUseCase.retryDeleteFileInCloud(file);

        // Assert
        verify(file, times(1)).incrementRetryCount();
        verify(fileStorageServicePort, times(1)).deleteFileInCloud(file);
        verify(file).setNextRetryAt(any());
        verify(fileRepositoryPort, times(1)).save(file);
        verify(fileRepositoryPort, never()).deleteById(any());
    }
}
