package org.naho.file.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.mapper.StoredFileMapper;
import org.naho.file.model.File;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.port.out.FileResultMapperPort;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.shared.exception.ApplicationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadFileToCloudTest {

    @Mock
    private FileRepositoryPort fileRepositoryPort;

    @Mock
    private FileStorageServicePort fileStorageServicePort;

    @Mock
    private FileResultMapperPort fileResultMapperPort;

    @Mock
    private StoredFileMapper storedFileMapper;

    @InjectMocks
    private UploadFileUseCase uploadFileUseCase;

    @Test
    @DisplayName("UTCID01 - Upload file lên cloud thành công khi storedFile hợp lệ")
    void UTCID01_UploadFileToCloudSuccess() {
        // Arrange
        String objectKey = "files/audio.mp3";
        StoredFile storedFile = mock(StoredFile.class);
        when(storedFile.objectKey()).thenReturn(objectKey);

        File file = mock(File.class);
        File savedFile = mock(File.class);
        FileResult expectedResult = mock(FileResult.class);

        when(fileRepositoryPort.findByObjectKey(objectKey)).thenReturn(file);
        when(fileRepositoryPort.save(file)).thenReturn(savedFile);
        when(fileResultMapperPort.domainToResult(savedFile)).thenReturn(expectedResult);

        // Act
        FileResult result = uploadFileUseCase.uploadFileToCloud(storedFile);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(fileRepositoryPort, times(1)).findByObjectKey(objectKey);
        verify(fileStorageServicePort, times(1)).uploadFileToCloud(storedFile);
        verify(fileStorageServicePort, times(1)).deleteFileInLocal(objectKey);
        verify(file).markCompleted();
        verify(fileRepositoryPort, times(1)).save(file);
        verify(fileResultMapperPort, times(1)).domainToResult(savedFile);
    }

    @Test
    @DisplayName("UTCID02 - Upload file lên cloud thất bại khi storedFile bị null")
    void UTCID02_StoredFileNull() {
        // Arrange
        StoredFile storedFile = null;

        // Act & Assert
        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> uploadFileUseCase.uploadFileToCloud(storedFile)
        );

        assertEquals(FileErrorCode.FILE_NOT_VALID, exception.getErrorCode());
        assertEquals(FileDetailMessageKey.FILE_NOT_VALID, exception.getMessage());
        verify(fileRepositoryPort, never()).findByObjectKey(any());
        verify(fileStorageServicePort, never()).uploadFileToCloud(any());
    }

    @Test
    @DisplayName("UTCID03 - Upload file lên cloud gặp lỗi ngoại lệ, đánh dấu FAILED và hẹn lịch retry")
    void UTCID03_CloudStorageError() {
        // Arrange
        String objectKey = "files/audio.mp3";
        StoredFile storedFile = mock(StoredFile.class);
        when(storedFile.objectKey()).thenReturn(objectKey);

        File file = mock(File.class);
        when(file.getRetryCount()).thenReturn(0);
        File savedFile = mock(File.class);
        FileResult expectedResult = mock(FileResult.class);

        when(fileRepositoryPort.findByObjectKey(objectKey)).thenReturn(file);
        doThrow(new RuntimeException("Cloud upload error"))
                .when(fileStorageServicePort).uploadFileToCloud(storedFile);
        when(fileRepositoryPort.save(file)).thenReturn(savedFile);
        when(fileResultMapperPort.domainToResult(savedFile)).thenReturn(expectedResult);

        // Act
        FileResult result = uploadFileUseCase.uploadFileToCloud(storedFile);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(fileRepositoryPort, times(1)).findByObjectKey(objectKey);
        verify(fileStorageServicePort, times(1)).uploadFileToCloud(storedFile);
        verify(fileStorageServicePort, never()).deleteFileInLocal(any());
        verify(file).markFailed();
        verify(file).setNextRetryAt(any());
        verify(fileRepositoryPort, times(1)).save(file);
    }
}
