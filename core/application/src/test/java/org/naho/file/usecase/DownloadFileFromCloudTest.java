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
import org.naho.file.result.DownloadedFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DownloadFileFromCloudTest {

    @Mock
    private FileRepositoryPort fileRepositoryPort;

    @Mock
    private FileStorageServicePort fileStorageServicePort;

    @InjectMocks
    private DownloadFileUseCase downloadFileUseCase;

    @Test
    @DisplayName("UTCID01 - Tải file từ cloud thành công khi objectKey hợp lệ và file tồn tại")
    void UTCID01_DownloadFileFromCloudSuccess() {
        // Arrange
        String objectKey = "files/doc.pdf";
        File file = mock(File.class);
        DownloadedFile downloadedFile = mock(DownloadedFile.class);

        when(fileRepositoryPort.findByObjectKey(objectKey)).thenReturn(file);
        when(fileStorageServicePort.downloadFileFromCloud(file)).thenReturn(downloadedFile);

        // Act
        DownloadedFile result = downloadFileUseCase.downloadFileFromCloud(objectKey);

        // Assert
        assertNotNull(result);
        assertEquals(downloadedFile, result);
        verify(fileRepositoryPort, times(1)).findByObjectKey(objectKey);
        verify(fileStorageServicePort, times(1)).downloadFileFromCloud(file);
    }

    @Test
    @DisplayName("UTCID02 - Tải file từ cloud thất bại khi objectKey bị null")
    void UTCID02_ObjectKeyNull() {
        // Arrange
        String objectKey = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> downloadFileUseCase.downloadFileFromCloud(objectKey)
        );

        assertEquals(FileErrorCode.FILE_NOT_VALID, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY, exception.getMessage());
        verify(fileRepositoryPort, never()).findByObjectKey(any());
        verify(fileStorageServicePort, never()).downloadFileFromCloud(any());
    }

    @Test
    @DisplayName("UTCID03 - Tải file từ cloud thất bại khi objectKey bị rỗng")
    void UTCID03_ObjectKeyBlank() {
        // Arrange
        String objectKey = "   ";

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> downloadFileUseCase.downloadFileFromCloud(objectKey)
        );

        assertEquals(FileErrorCode.FILE_NOT_VALID, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY, exception.getMessage());
        verify(fileRepositoryPort, never()).findByObjectKey(any());
        verify(fileStorageServicePort, never()).downloadFileFromCloud(any());
    }

    @Test
    @DisplayName("UTCID04 - Tải file từ cloud thất bại khi không tìm thấy file trong DB với objectKey chỉ định")
    void UTCID04_FileNotFound() {
        // Arrange
        String objectKey = "files/nonexistent.pdf";

        when(fileRepositoryPort.findByObjectKey(objectKey)).thenReturn(null);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> downloadFileUseCase.downloadFileFromCloud(objectKey)
        );

        assertEquals(FileErrorCode.FILE_NOT_FOUND, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_NOT_FOUND, exception.getMessage());
        verify(fileRepositoryPort, times(1)).findByObjectKey(objectKey);
        verify(fileStorageServicePort, never()).downloadFileFromCloud(any());
    }
}
