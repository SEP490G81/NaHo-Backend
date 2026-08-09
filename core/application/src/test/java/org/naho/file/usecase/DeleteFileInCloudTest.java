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
import org.naho.file.type.OperationType;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteFileInCloudTest {

    @Mock
    private FileRepositoryPort fileRepositoryPort;

    @Mock
    private FileStorageServicePort fileStorageServicePort;

    @InjectMocks
    private DeleteFileUseCase deleteFileUseCase;

    @Test
    @DisplayName("UTCID01 - Xóa file trên cloud thành công khi objectKey hợp lệ và operationType là DELETE")
    void UTCID01_DeleteFileInCloudSuccess() {
        // Arrange
        String objectKey = "files/sample.png";
        Long fileId = 10L;

        File file = mock(File.class);
        when(file.getId()).thenReturn(fileId);
        when(file.getOperationType()).thenReturn(OperationType.DELETE);
        when(fileRepositoryPort.findByObjectKey(objectKey)).thenReturn(file);

        // Act
        deleteFileUseCase.deleteFileInCloud(objectKey);

        // Assert
        verify(fileRepositoryPort, times(1)).findByObjectKey(objectKey);
        verify(fileStorageServicePort, times(1)).deleteFileInCloud(file);
        verify(fileRepositoryPort, times(1)).deleteById(fileId);
    }

    @Test
    @DisplayName("UTCID02 - Xóa file trên cloud thất bại khi objectKey bị null")
    void UTCID02_ObjectKeyNull() {
        // Arrange
        String objectKey = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> deleteFileUseCase.deleteFileInCloud(objectKey)
        );

        assertEquals(FileErrorCode.FILE_NOT_VALID, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY, exception.getMessage());
        verify(fileRepositoryPort, never()).findByObjectKey(any());
        verify(fileStorageServicePort, never()).deleteFileInCloud(any());
    }

    @Test
    @DisplayName("UTCID03 - Xóa file trên cloud thất bại khi objectKey bị rỗng")
    void UTCID03_ObjectKeyBlank() {
        // Arrange
        String objectKey = "   ";

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> deleteFileUseCase.deleteFileInCloud(objectKey)
        );

        assertEquals(FileErrorCode.FILE_NOT_VALID, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_OBJECT_KEY_EMPTY, exception.getMessage());
        verify(fileRepositoryPort, never()).findByObjectKey(any());
        verify(fileStorageServicePort, never()).deleteFileInCloud(any());
    }

    @Test
    @DisplayName("UTCID04 - Xóa file trên cloud thất bại khi operationType không phải là DELETE")
    void UTCID04_OperationTypeNotDelete() {
        // Arrange
        String objectKey = "files/sample.png";
        File file = mock(File.class);
        when(file.getOperationType()).thenReturn(OperationType.UPLOAD);
        when(fileRepositoryPort.findByObjectKey(objectKey)).thenReturn(file);

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> deleteFileUseCase.deleteFileInCloud(objectKey)
        );

        assertEquals(FileErrorCode.FILE_NOT_VALID, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_NOT_VALID, exception.getMessage());
        verify(fileRepositoryPort, times(1)).findByObjectKey(objectKey);
        verify(fileStorageServicePort, never()).deleteFileInCloud(any());
        verify(fileRepositoryPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("UTCID05 - Xóa file trên cloud gặp lỗi ngoại lệ, cập nhật trạng thái FAILED và hẹn lịch retry")
    void UTCID05_CloudStorageError() {
        // Arrange
        String objectKey = "files/sample.png";
        File file = mock(File.class);
        when(file.getOperationType()).thenReturn(OperationType.DELETE);
        when(file.getRetryCount()).thenReturn(0);
        when(fileRepositoryPort.findByObjectKey(objectKey)).thenReturn(file);

        doThrow(new RuntimeException("Cloud error"))
                .when(fileStorageServicePort).deleteFileInCloud(file);

        // Act
        deleteFileUseCase.deleteFileInCloud(objectKey);

        // Assert
        verify(fileRepositoryPort, times(1)).findByObjectKey(objectKey);
        verify(fileStorageServicePort, times(1)).deleteFileInCloud(file);
        verify(file).markFailed();
        verify(file).setNextRetryAt(any());
        verify(fileRepositoryPort, times(1)).save(file);
        verify(fileRepositoryPort, never()).deleteById(any());
    }
}
