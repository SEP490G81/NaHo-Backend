package org.naho.file.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.FileResultMapper;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteFileByIdTest {

    @Mock
    private FileStorageServicePort fileStorageServicePort;

    @Mock
    private FileRepositoryPort fileRepositoryPort;

    @Mock
    private FileResultMapper fileResultMapper;

    @InjectMocks
    private FileStorageUseCase fileStorageUseCase;

    @Test
    void UTCID01_Should_DeleteFile_Successfully() {
        // Arrange (Given)
        Long id = 1L;
        String objectKey = "avatars/uuid-timestamp";

        when(fileRepositoryPort.findObjectKeyById(id))
                .thenReturn(objectKey);

        doNothing().when(fileRepositoryPort).deleteById(id);
        doNothing().when(fileStorageServicePort).deleteFileByObjectKey(objectKey);

        // Act (When)
        String result = fileStorageUseCase.deleteFileById(id);

        // Assert (Then)
        assertEquals(objectKey, result);

        verify(fileRepositoryPort, times(1))
                .findObjectKeyById(id);

        verify(fileRepositoryPort, times(1))
                .deleteById(id);

        verify(fileStorageServicePort, times(1))
                .deleteFileByObjectKey(objectKey);

        verifyNoMoreInteractions(fileRepositoryPort, fileStorageServicePort);
    }

    @Test
    void UTCID02_Should_ThrowException_When_FileIdNull() {
        // Arrange (Given)
        Long id = null;

        // Act (When) & Assert (Then)
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> fileStorageUseCase.deleteFileById(id)
        );

        assertEquals(FileErrorCode.FILE_NOT_FOUND, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_ID_NULL, exception.getMessage());

        verifyNoInteractions(fileRepositoryPort, fileStorageServicePort);
    }

    @Test
    void UTCID03_Should_ThrowException_When_RepositoryDeleteFails() {
        // Arrange (Given)
        Long id = 1L;
        String objectKey = "avatars/uuid-timestamp";

        when(fileRepositoryPort.findObjectKeyById(id))
                .thenReturn(objectKey);

        doThrow(new RuntimeException("Database connection error"))
                .when(fileRepositoryPort).deleteById(id);

        // Act (When) & Assert (Then)
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> fileStorageUseCase.deleteFileById(id)
        );

        assertEquals("Database connection error", exception.getMessage());

        verify(fileRepositoryPort, times(1))
                .findObjectKeyById(id);

        verify(fileRepositoryPort, times(1))
                .deleteById(id);

        verifyNoMoreInteractions(fileRepositoryPort);
        verifyNoInteractions(fileStorageServicePort);
    }

    @Test
    void UTCID04_Should_ThrowException_When_StorageDeleteFails() {
        // Arrange (Given)
        Long id = 1L;
        String objectKey = "avatars/uuid-timestamp";

        when(fileRepositoryPort.findObjectKeyById(id))
                .thenReturn(objectKey);

        doNothing().when(fileRepositoryPort).deleteById(id);

        doThrow(new RuntimeException("S3 connection error"))
                .when(fileStorageServicePort).deleteFileByObjectKey(objectKey);

        // Act (When) & Assert (Then)
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> fileStorageUseCase.deleteFileById(id)
        );

        assertEquals("S3 connection error", exception.getMessage());

        verify(fileRepositoryPort, times(1))
                .findObjectKeyById(id);

        verify(fileRepositoryPort, times(1))
                .deleteById(id);

        verify(fileStorageServicePort, times(1))
                .deleteFileByObjectKey(objectKey);

        verifyNoMoreInteractions(fileRepositoryPort, fileStorageServicePort);
    }
}
